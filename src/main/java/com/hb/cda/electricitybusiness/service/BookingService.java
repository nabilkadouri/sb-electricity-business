package com.hb.cda.electricitybusiness.service;

import com.hb.cda.electricitybusiness.dto.BookingRequest;
import com.hb.cda.electricitybusiness.dto.BookingResponse;
import com.hb.cda.electricitybusiness.dto.mapper.BookingMapper;
import com.hb.cda.electricitybusiness.enums.DayOfWeek;
import com.hb.cda.electricitybusiness.model.Booking;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.Timeslot;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.BookingRepository;
import com.hb.cda.electricitybusiness.repository.ChargingStationRepository;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private BookingMapper bookingMapper;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ChargingStationRepository chargingStationRepository;

    public BookingService(BookingMapper bookingMapper, BookingRepository bookingRepository, UserRepository userRepository, ChargingStationRepository chargingStationRepository) {
        this.bookingMapper = bookingMapper;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.chargingStationRepository = chargingStationRepository;
    }

   /* @Transactional
    public BookingResponse creatBooking (BookingRequest request) {
        Booking booking = bookingMapper.toEntity(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + request.getUserId()));
        booking.setUser(user);

        ChargingStation chargingStation = chargingStationRepository.findById(request.getChargingStationId())
                .orElseThrow(() -> new RuntimeException("Borne non trouvé avec l'ID: " + request.getChargingStationId()));
        booking.setChargingStation(chargingStation);
    }*/

        private BigDecimal CalculateTotalAmount(
                LocalDateTime bookingStartDate,
                LocalDateTime bookingEndDateTime,
                BigDecimal pricePerHour,
                List<Timeslot> stationTimeslots
    ) {

            //Initialiser le total des heures facturable
            BigDecimal totalChargeableHours = BigDecimal.ZERO;
            //Jours
            LocalDateTime currentDayIterator = bookingStartDate;

            while (currentDayIterator.isBefore(bookingEndDateTime)) {


                java.time.DayOfWeek dayOfWeekJava = currentDayIterator.getDayOfWeek();

                DayOfWeek dayOfWeekEnum = DayOfWeek.valueOf(dayOfWeekJava.name());
                LocalDateTime startOfPeriodForThisDay = currentDayIterator;
                LocalDateTime endOfPeriodForThisDay = bookingEndDateTime.isBefore(currentDayIterator.toLocalDate().atTime(LocalTime.MAX)) ?
                        bookingEndDateTime : currentDayIterator.toLocalDate().atTime(LocalTime.MAX);

                List<Timeslot> availableTimeslotsForThisDay = stationTimeslots.stream()
                        .filter(ts -> ts.getDayOfWeek() == dayOfWeekEnum && ts.getIsAvailable())
                        .collect(Collectors.toList());

                BigDecimal hoursToChargeForThisDay = BigDecimal.ZERO;

                for (Timeslot timeslot : availableTimeslotsForThisDay) {

                    LocalDateTime timeslotPeriodStart = currentDayIterator.toLocalDate().atTime(timeslot.getStartTime().toLocalTime());
                    LocalDateTime timeslotPeriodEnd = currentDayIterator.toLocalDate().atTime(timeslot.getEndTime().toLocalTime());

                    if (timeslotPeriodEnd.isBefore(timeslotPeriodStart)) {
                        timeslotPeriodEnd = timeslotPeriodEnd.plusDays(1);
                    }

                    LocalDateTime overlapStart = (startOfPeriodForThisDay.isAfter(timeslotPeriodStart)) ? startOfPeriodForThisDay : timeslotPeriodStart;
                    LocalDateTime overlapEnd = (endOfPeriodForThisDay.isBefore(timeslotPeriodEnd)) ? endOfPeriodForThisDay : timeslotPeriodEnd;


                    if (overlapStart.isBefore(overlapEnd)) {
                        Duration durationOfOverlap = Duration.between(overlapStart, overlapEnd);
                        // Convertir la durée de chevauchement en heures (BigDecimal pour la précision)
                        BigDecimal hoursInOverlap = BigDecimal.valueOf(durationOfOverlap.toMillis())
                                .divide(BigDecimal.valueOf(1000 * 60 * 60), 2, BigDecimal.ROUND_HALF_UP);
                        hoursToChargeForThisDay = hoursToChargeForThisDay.add(hoursInOverlap);
                    }
                }
                // Ajouter les heures facturables de ce jour au total général
                totalChargeableHours = totalChargeableHours.add(hoursToChargeForThisDay);

                // 6. Préparer pour le jour suivant : avancer l'itérateur au début du jour d'après
                currentDayIterator = currentDayIterator.plusDays(1).toLocalDate().atStartOfDay();
            }

            // 7. Calculer le montant total final : total des heures * prix par heure
            BigDecimal finalTotalAmount = pricePerHour.multiply(totalChargeableHours);

            // Arrondir à 2 décimales pour un montant monétaire standard
            return finalTotalAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
        }


}
