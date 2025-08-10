package com.hb.cda.electricitybusiness.business.impl;

import com.hb.cda.electricitybusiness.business.TimeslotBusiness;
import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.controller.dto.TimeslotRequest;
import com.hb.cda.electricitybusiness.controller.dto.TimeslotResponse;
import com.hb.cda.electricitybusiness.controller.dto.mapper.TimeslotMapper;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.Timeslot;
import com.hb.cda.electricitybusiness.repository.ChargingStationRepository;
import com.hb.cda.electricitybusiness.repository.TimeslotRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeslotBusinessImpl implements TimeslotBusiness {

    private TimeslotRepository timeslotRepository;
    private ChargingStationRepository chargingStationRepository;
    private TimeslotMapper timeslotMapper;

    public TimeslotBusinessImpl(TimeslotRepository timeslotRepository, ChargingStationRepository chargingStationRepository, TimeslotMapper timeslotMapper) {
        this.timeslotRepository = timeslotRepository;
        this.chargingStationRepository = chargingStationRepository;
        this.timeslotMapper = timeslotMapper;
    }

    @Override
    public List<TimeslotResponse> createMultipleTimeslots(List<TimeslotRequest> timeslotRequests) throws BusinessException {
        if(timeslotRequests == null || timeslotRequests.isEmpty()) {
            throw new BusinessException("La liste des créneaux ne peut pas être vide.");
        }

        List<Timeslot> timeslotsToSave = timeslotRequests.stream()
                .map(requestDto -> {
                    ChargingStation chargingStation = chargingStationRepository.findById(requestDto.getChargingStationId())
                            .orElseThrow(() -> new BusinessException("Borne de recharge non trouvé avec l'id: " + requestDto.getChargingStationId()));
                    Timeslot timeslot = timeslotMapper.convertToEntity(requestDto);
                    timeslot.setChargingStation(chargingStation);

                    return timeslot;
                })
                .collect((Collectors.toList()));

        List<Timeslot> savedTimeslots = timeslotRepository.saveAll(timeslotsToSave);

        return timeslotMapper.toResponseList(savedTimeslots);
    }

    @Override
    public TimeslotResponse createTimeslot(TimeslotRequest request) throws BusinessException {
        // 1. Récupérer l'id de chargingStation
        ChargingStation chargingStation = chargingStationRepository.findById(request.getChargingStationId())
                .orElseThrow(() -> new RuntimeException("Borne de recharge non trouvée avec l'ID: " + request.getChargingStationId()));

        Timeslot timeslot = timeslotMapper.convertToEntity(request);
        timeslot.setChargingStation(chargingStation);

        Timeslot newTimeslot = timeslotRepository.save(timeslot);
        return timeslotMapper.toResponse(newTimeslot);
    }

    @Override
    public List<TimeslotResponse> getAllTimeslots() {
        return timeslotRepository.findAll().stream()
                .map(timeslotMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TimeslotResponse getTimeslotById(Long id) throws BusinessException {
        Timeslot timeslot = timeslotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Créneau horaire non trouvé avec l'ID: " + id));
        return timeslotMapper.toResponse(timeslot);
    }


    @Override
    public TimeslotResponse updateTimeslot(Long id, TimeslotRequest request) throws BusinessException {
        Timeslot existingTimeslot = timeslotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Créneau horaire non trouvé avec l'ID: " + id));
        timeslotMapper.updateEntityFromRequest(request, existingTimeslot);
        Timeslot updatedTimeslot = timeslotRepository.save(existingTimeslot);
        return timeslotMapper.toResponse(updatedTimeslot);
    }

    @Override
    public void deleteTimeslot(Long id) throws BusinessException {
        if (!timeslotRepository.existsById(id)) {
            throw new BusinessException("Créneau horaire non trouvé avec l'ID: " + id);
        }
        timeslotRepository.deleteById(id);

    }
}
