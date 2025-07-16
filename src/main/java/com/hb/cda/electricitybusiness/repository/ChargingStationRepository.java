package com.hb.cda.electricitybusiness.repository;

import com.hb.cda.electricitybusiness.model.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {
    List<ChargingStation> findByUserId(Long userId);
}
