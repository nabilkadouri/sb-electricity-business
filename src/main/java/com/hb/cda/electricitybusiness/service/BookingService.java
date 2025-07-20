package com.hb.cda.electricitybusiness.service;

import com.hb.cda.electricitybusiness.dto.BookingRequest;
import com.hb.cda.electricitybusiness.dto.BookingResponse;
import com.hb.cda.electricitybusiness.dto.mapper.BookingMapper;
import com.hb.cda.electricitybusiness.model.Booking;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.Timeslot;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.BookingRepository;
import com.hb.cda.electricitybusiness.repository.ChargingStationRepository;
import com.hb.cda.electricitybusiness.repository.TimeslotRepository;
import com.hb.cda.electricitybusiness.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class BookingService {
    private BookingMapper bookingMapper;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ChargingStationRepository chargingStationRepository;
    private TimeslotRepository timeslotRepository;

    public BookingService(BookingMapper bookingMapper, BookingRepository bookingRepository, UserRepository userRepository, ChargingStationRepository chargingStationRepository, TimeslotRepository timeslotRepository) {
        this.bookingMapper = bookingMapper;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.chargingStationRepository = chargingStationRepository;
        this.timeslotRepository = timeslotRepository;
    }

   @Transactional
    public BookingResponse createBooking (BookingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + request.getUserId()));


        ChargingStation chargingStation = chargingStationRepository.findById(request.getChargingStationId())
                .orElseThrow(() -> new RuntimeException("Borne non trouvé avec l'ID: " + request.getChargingStationId()));

        List<Timeslot> existingTimeslots = timeslotRepository.findByChargingStationId(chargingStation.getId());
       System.out.println(existingTimeslots);

        LocalDateTime requestedStart = request.getStartDate();
        LocalDateTime requestedEnd = request.getEndDate();

        for (Timeslot timeslot: existingTimeslots) {
            if(!timeslot.getStartTime().isAfter(requestedEnd) && timeslot.getEndTime().isBefore(requestedStart)) {
                if (!timeslot.getIsAvailable()) {
                    throw new RuntimeException("La borne de recharge n'est pas disponible pour la période demandée. Timeslot " + timeslot.getId() + " est déjà réservé.");
                }
            }
        }

        Booking booking = bookingMapper.convertToEntity(request);

        booking.setUser(user);
        booking.setChargingStation(chargingStation);
        booking.setTotalAmount(calculateTotalAmount(requestedStart,requestedEnd, chargingStation.getPricePerHour()));

        //Mise à jour des timeslots comme indisponible
       for (Timeslot timeslot : existingTimeslots) {
           boolean overlaps = !timeslot.getStartTime().isAfter(requestedEnd) &&
                   !timeslot.getEndTime().isBefore(requestedStart);
           if (overlaps) { // Condition simplifiée
               timeslot.setIsAvailable(false);
               timeslotRepository.save(timeslot);
           }
       }

        Booking newBooking = bookingRepository.save(booking);
        return bookingMapper.ToResponse(newBooking);
    }

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

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID : " + id));
        return bookingMapper.ToResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(bookingMapper::ToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookingByUserId (Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID :" + userId));

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());

        return bookings.stream()
                .map(bookingMapper::ToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookingByChargingStationId (Long chargingStationId) {
        ChargingStation chargingStation = chargingStationRepository.findById(chargingStationId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID :" + chargingStationId));

        List<Booking> bookings = bookingRepository.findByUserId(chargingStation.getId());

        return bookings.stream()
                .map(bookingMapper::ToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBooking(Long id) {
        //Récupérer id de la réservation
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvé avec l'ID :" +
                 id));

        //Récupérer la borne en lien avec la réservation
        ChargingStation chargingStation = booking.getChargingStation();

        //Récupérer les timslots de la borne
        List<Timeslot> timeslots = timeslotRepository.findByChargingStationId(chargingStation.getId());

        LocalDateTime bookingStart = booking.getStartDate();
        LocalDateTime bookingEnd = booking.getEndDate();

        for (Timeslot timeslot : timeslots) {
            if(!timeslot.getStartTime().isAfter(bookingEnd) && !timeslot.getEndTime().isBefore(bookingStart) && !timeslot.getIsAvailable()) {
                timeslot.setIsAvailable(true);
                timeslotRepository.save(timeslot);
            }
        }
        bookingRepository.delete(booking);
    }

}
