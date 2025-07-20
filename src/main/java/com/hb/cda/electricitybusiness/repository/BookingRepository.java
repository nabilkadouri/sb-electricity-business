package com.hb.cda.electricitybusiness.repository;

import com.hb.cda.electricitybusiness.enums.BookingStatus;
import com.hb.cda.electricitybusiness.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //Afficher toutes les réservations d'un user
    List<Booking> findByUserId(Long userId);

    //Afficher toutes les réservations d'une borne donnée
    List<Booking> findByChargingStationId(Long chargingStationId);


    // Trouver les réservations pour une borne dans une plage de dates/heures donnée
    /*List<Booking> findByChargingStationIdAndStartDateBetween(Long chargingStationId, LocalDateTime start, LocalDateTime end);*/

}
