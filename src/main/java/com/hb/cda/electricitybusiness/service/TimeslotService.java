package com.hb.cda.electricitybusiness.service;

import com.hb.cda.electricitybusiness.dto.TimeslotRequest;
import com.hb.cda.electricitybusiness.dto.TimeslotResponse;
import com.hb.cda.electricitybusiness.dto.mapper.TimeslotMapper;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.Timeslot;
import com.hb.cda.electricitybusiness.repository.ChargingStationRepository;
import com.hb.cda.electricitybusiness.repository.TimeslotRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeslotService {

    private  TimeslotRepository timeslotRepository;
    private  ChargingStationRepository chargingStationRepository;
    private  TimeslotMapper timeslotMapper;

    public TimeslotService(TimeslotRepository timeslotRepository, ChargingStationRepository chargingStationRepository, TimeslotMapper timeslotMapper) {
        this.timeslotRepository = timeslotRepository;
        this.chargingStationRepository = chargingStationRepository;
        this.timeslotMapper = timeslotMapper;
    }

    @Transactional
    public TimeslotResponse createTimeslot(TimeslotRequest request) {
        // 1. Récupérer l'id de chargingStation
        ChargingStation chargingStation = chargingStationRepository.findById(request.getChargingStationId())
                .orElseThrow(() -> new RuntimeException("Borne de recharge non trouvée avec l'ID: " + request.getChargingStationId()));

        Timeslot timeslot = timeslotMapper.convertToEntity(request);
        timeslot.setChargingStation(chargingStation);

        Timeslot newTimeslot = timeslotRepository.save(timeslot);
        return timeslotMapper.toResponse(newTimeslot);
    }

    public List<TimeslotResponse> getAllTimeslots() {
        return timeslotRepository.findAll().stream()
                .map(timeslotMapper::toResponse)
                .collect(Collectors.toList());
    }

    public TimeslotResponse getTimeslotById(Long id) {
        Timeslot timeslot = timeslotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Créneau horaire non trouvé avec l'ID: " + id));
        return timeslotMapper.toResponse(timeslot);
    }

    public List<TimeslotResponse> getAvailableTimeslotsByChargingStation(Long chargingStationId) {
        return timeslotRepository.findByChargingStationIdAndIsAvailableTrue(chargingStationId).stream()
                .map(timeslotMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TimeslotResponse updateTimeslot(Long id, TimeslotRequest request) {
        Timeslot existingTimeslot = timeslotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Créneau horaire non trouvé avec l'ID: " + id));
        timeslotMapper.updateEntityFromRequest(request, existingTimeslot);
        Timeslot updatedTimeslot = timeslotRepository.save(existingTimeslot);
        return timeslotMapper.toResponse(updatedTimeslot);
    }

    @Transactional
    public void deleteTimeslot(Long id) {
        if (!timeslotRepository.existsById(id)) {
            throw new RuntimeException("Créneau horaire non trouvé avec l'ID: " + id);
        }
        timeslotRepository.deleteById(id);
    }
}

