package com.hb.cda.electricitybusiness.business.impl;

import com.hb.cda.electricitybusiness.business.BookingBusiness;
import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.controller.dto.BookingRequest;
import com.hb.cda.electricitybusiness.controller.dto.BookingResponse;
import com.hb.cda.electricitybusiness.controller.dto.BookingStatusUpdateRequest;
import com.hb.cda.electricitybusiness.controller.dto.mapper.BookingMapper;
import com.hb.cda.electricitybusiness.enums.BookingStatus;
import com.hb.cda.electricitybusiness.messaging.MailService;
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
    private MailService mailService;

    public BookingBusinessImpl(BookingMapper bookingMapper, BookingRepository bookingRepository, UserRepository userRepository, ChargingStationRepository chargingStationRepository, MailService mailService) {
        this.bookingMapper = bookingMapper;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.chargingStationRepository = chargingStationRepository;
        this.mailService = mailService;
    }

    // Création d’une réservation
    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) throws BusinessException {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        ChargingStation chargingStation = chargingStationRepository.findById(request.getChargingStationId())
                .orElseThrow(() -> new BusinessException("Borne non trouvée"));

        if (Boolean.FALSE.equals(chargingStation.getIsAvailable())) {
            throw new BusinessException("La borne n’est pas disponible.");
        }

        LocalDateTime requestedStart = request.getStartDate();
        LocalDateTime requestedEnd = request.getEndDate();

        if (!requestedEnd.isAfter(requestedStart)) {
            throw new BusinessException("L’heure de fin doit être après l’heure de début.");
        }

        // Vérification correspondance timeslot
        if (!isValidTimeslot(chargingStation.getTimeslots(), requestedStart, requestedEnd)) {
            throw new BusinessException("La période demandée ne correspond à aucun créneau configuré.");
        }

        // Chevauchements
        for (Booking existing : chargingStation.getBookings()) {
            boolean overlap = requestedStart.isBefore(existing.getEndDate()) &&
                    requestedEnd.isAfter(existing.getStartDate());
            if (overlap) {
                throw new BusinessException("Ce créneau est déjà réservé.");
            }
        }

        // Création du booking
        Booking booking = bookingMapper.convertToEntity(request);
        booking.setUser(user);
        booking.setChargingStation(chargingStation);
        booking.setStartDate(requestedStart);
        booking.setEndDate(requestedEnd);
        booking.setTotalAmount(
                calculateTotalAmount(requestedStart, requestedEnd, chargingStation.getPricePerHour())
        );

        Booking newBooking = bookingRepository.save(booking);

        // EMAIL → Notification au locataire
        mailService.sendReservationCreatedEmail(
                user.getEmail(),
                chargingStation.getNameStation(),
                requestedStart.toLocalDate().toString(),
                requestedStart.toLocalTime().toString(),
                requestedEnd.toLocalTime().toString()
        );

        // EMAIL → Notification au propriétaire
        mailService.notifyOwnerPendingBooking(newBooking);

        return bookingMapper.ToResponse(newBooking);
    }


    // Récupération une réservation par son ID
    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) throws BusinessException{
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Réservation non trouvée avec l'ID : " + id));
        return bookingMapper.ToResponse(booking);
    }


    // Récupération toutes les réservations d'une borne
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookingByChargingStationId(Long stationId) throws BusinessException {

        chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new BusinessException("Borne introuvable"));

        List<Booking> bookings = bookingRepository.findByChargingStationId(stationId);

        return bookings.stream()
                .map(bookingMapper::ToResponse)
                .collect(Collectors.toList());
    }

    // Récupération toutes les réservations
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(bookingMapper::ToResponse)
                .collect(Collectors.toList());
    }


    // Récupération toutes les réservations d'un utilisateur
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



    // Modification statut (Confirmé / Annulé)
    @Override
    public Booking updateBookingStatus(Long id, BookingStatusUpdateRequest request) throws BusinessException {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Réservation introuvable"));

        BookingStatus newStatus = BookingStatus.fromDisplayValue(request.getStatus());

        booking.setStatus(newStatus);

        if (newStatus == BookingStatus.CANCELLED) {
            booking.setCancelReason(request.getCancelReason());
        }

        Booking saved = bookingRepository.save(booking);

        // EMAIL LOCATAIRE
        switch (newStatus) {
            case CONFIRMED -> mailService.notifyUserBookingConfirmed(saved);
            case CANCELLED -> mailService.notifyUserBookingCancelled(saved);
        }

        return saved;
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


    // Méthode de calcul du total d'une réservation de borne (prix + frais 2,5 %)
    private static BigDecimal calculateTotalAmount(LocalDateTime startTime, LocalDateTime endTime, BigDecimal pricePerHour) {

        // Calcul de la durée en minutes
        Duration duration = Duration.between(startTime, endTime);
        double hours = duration.toMinutes() / 60.0;

        // Sécuriser pricePerHour (éviter les null)
        BigDecimal rate = pricePerHour != null ? pricePerHour : BigDecimal.ZERO;

        // Calcul du prix de base (ex: 7.8 × 1.5 = 11.70)
        BigDecimal basePrice = rate.multiply(BigDecimal.valueOf(hours));

        // Calcul des frais de réservation (2.5 %)
        BigDecimal fee = basePrice.multiply(new BigDecimal("0.025"));

        // Total TTC = prix + frais
        BigDecimal total = basePrice.add(fee);

        // Arrondi à 2 décimales
        return total.setScale(2, RoundingMode.HALF_UP);
    }


    /**
     * Vérifie si la période demandée est contenue dans un créneau horaire valide de la borne.
     */
    private boolean isValidTimeslot(List<Timeslot> timeslots, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        if (timeslots == null || timeslots.isEmpty()) {
            return false;
        }

        // Conversion automatique
        com.hb.cda.electricitybusiness.enums.DayOfWeek requestDay =
                com.hb.cda.electricitybusiness.enums.DayOfWeek.valueOf(
                        requestedStart.getDayOfWeek().name()
                );
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
