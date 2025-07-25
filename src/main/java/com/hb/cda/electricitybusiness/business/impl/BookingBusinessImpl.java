package com.hb.cda.electricitybusiness.business.impl;

import com.hb.cda.electricitybusiness.business.BookingBusiness;
import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.dto.BookingRequest;
import com.hb.cda.electricitybusiness.dto.BookingResponse;
import com.hb.cda.electricitybusiness.dto.mapper.BookingMapper;
import com.hb.cda.electricitybusiness.model.Booking;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.Timeslot;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.BookingRepository;
import com.hb.cda.electricitybusiness.repository.ChargingStationRepository;
import com.hb.cda.electricitybusiness.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class BookingBusinessImpl implements BookingBusiness {
    private BookingMapper bookingMapper;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ChargingStationRepository chargingStationRepository;

    public BookingBusinessImpl(BookingMapper bookingMapper, BookingRepository bookingRepository, UserRepository userRepository, ChargingStationRepository chargingStationRepository) {
        this.bookingMapper = bookingMapper;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.chargingStationRepository = chargingStationRepository;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) throws BusinessException {
        // Récupérer l'utilisateur
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé avec l'ID: " + request.getUserId()));

        // Récupérer la borne
        ChargingStation chargingStation = chargingStationRepository.findById(request.getChargingStationId())
                .orElseThrow(() -> new BusinessException("Borne non trouvée avec l'ID: " + request.getChargingStationId()));

        // Vérifier la disponibilité de la borne
        if (chargingStation.getIsAvailable() == null || !chargingStation.getIsAvailable()) {
            throw new BusinessException("La borne de recharge '" + chargingStation.getNameStation() + "' n'est pas disponible.");
        }

        LocalDateTime requestedStartDateTime = request.getStartDate();
        LocalDateTime requestedEndDateTime = request.getEndDate();

        // Vérifier que l'heure de fin soit après l'heure de début
        if (requestedEndDateTime.isBefore(requestedStartDateTime) || requestedEndDateTime.isEqual(requestedStartDateTime)) {
            throw new BusinessException("L'heure de fin de la réservation doit être après l'heure de début.");
        }

        // Vérifier que la demande correspond à un créneau horaire valide
        if (!isValidTimeslot(chargingStation.getTimeslots(), requestedStartDateTime, requestedEndDateTime)) {
            throw new BusinessException("La période de réservation demandée ne correspond pas à un créneau horaire configuré pour cette borne.");
        }

        // Vérifier les chevauchements avec les réservations existantes
        List<Booking> existingBookingsForStation = chargingStation.getBookings();
        if (existingBookingsForStation != null) {
            for (Booking existingBooking : existingBookingsForStation) {
                boolean overlapsWithExistingBooking = requestedStartDateTime.isBefore(existingBooking.getEndDate()) &&
                        requestedEndDateTime.isAfter(existingBooking.getStartDate());

                if (overlapsWithExistingBooking) {
                    throw new BusinessException("La borne est déjà réservée pour une partie ou la totalité de la période demandée (" +
                            existingBooking.getStartDate().toLocalDate() + " " + existingBooking.getStartDate().toLocalTime() + " - " +
                            existingBooking.getEndDate().toLocalTime() + ").");
                }
            }
        }

        // Création du booking
        Booking booking = bookingMapper.convertToEntity(request);
        booking.setUser(user);
        booking.setChargingStation(chargingStation);
        booking.setStartDate(requestedStartDateTime);
        booking.setEndDate(requestedEndDateTime);
        booking.setTotalAmount(calculateTotalAmount(requestedStartDateTime, requestedEndDateTime, chargingStation.getPricePerHour()));

        Booking newBooking = bookingRepository.save(booking);
        return bookingMapper.ToResponse(newBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) throws BusinessException{
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Réservation non trouvée avec l'ID : " + id));
        return bookingMapper.ToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(bookingMapper::ToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookingByUserId(Long userId) throws BusinessException{
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé avec l'ID :" + userId));

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());

        return bookings.stream()
                .map(bookingMapper::ToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookingByChargingStationId(Long chargingStationId) throws BusinessException {
        ChargingStation chargingStation = chargingStationRepository.findById(chargingStationId)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé avec l'ID :" + chargingStationId));

        List<Booking> bookings = bookingRepository.findByUserId(chargingStation.getId());

        return bookings.stream()
                .map(bookingMapper::ToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) throws BusinessException{
        //Récupérer id de la réservation
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Réservation non trouvé avec l'ID :" +
                 id));
        bookingRepository.delete(booking);
    }


    // Methode de calcule du total d'une reservation de borne
    private static BigDecimal calculateTotalAmount(LocalDateTime startTime, LocalDateTime endTime, BigDecimal pricePerHour) {
        //Obtenir le nombre d'heures entre deux créneaux( en minute)
        Duration duration = Duration.between(startTime,endTime);

        //Convertir les minutes en heure
        double convertMinutesToHours = duration.toMinutes() / 60.0;

        BigDecimal finalPricePerHour;
        if(pricePerHour != null) {
            finalPricePerHour = pricePerHour;
        } else {
            finalPricePerHour = BigDecimal.ZERO;
        }

        BigDecimal totalAmount = finalPricePerHour.multiply(BigDecimal.valueOf(convertMinutesToHours));

        return totalAmount.setScale(2, RoundingMode.HALF_UP);
    }


    /**
     * Vérifie si la période demandée est contenue dans un créneau horaire valide de la borne.
     */
    private boolean isValidTimeslot(List<Timeslot> timeslots, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        if (timeslots == null || timeslots.isEmpty()) {
            return false;
        }

        DayOfWeek requestDay = requestedStart.getDayOfWeek();
        LocalTime startTime = requestedStart.toLocalTime();
        LocalTime endTime = requestedEnd.toLocalTime();

        for (Timeslot timeslot : timeslots) {
            if (timeslot.getDayOfWeek().equals(requestDay) &&
                    !startTime.isBefore(timeslot.getStartTime()) &&
                    !endTime.isAfter(timeslot.getEndTime())) {
                return true;
            }
        }
        return false;
    }



}
