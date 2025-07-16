package com.hb.cda.electricitybusiness.repository;

import com.hb.cda.electricitybusiness.model.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {
    List<Timeslot> findByChargingStationId(Long chargingStationId);
    List<Timeslot> findByChargingStationIdAndIsAvailableTrue(Long chargingStationId);
}
