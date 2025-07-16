package com.hb.cda.electricitybusiness.service;

import com.hb.cda.electricitybusiness.dto.ChargingStationRequest;
import com.hb.cda.electricitybusiness.dto.ChargingStationResponse;
import com.hb.cda.electricitybusiness.dto.LocationStationResponse;
import com.hb.cda.electricitybusiness.dto.mapper.ChargingStationMapper;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.LocationStation;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.ChargingStationRepository;
import com.hb.cda.electricitybusiness.repository.LocationStationRepository;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.xml.stream.Location;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChargingStationService {

    private ChargingStationRepository chargingStationRepository;
    private ChargingStationMapper chargingStationMapper;
    private UserRepository userRepository;
    private LocationStationRepository locationStationRepository;

    public ChargingStationService(ChargingStationRepository chargingStationRepository, ChargingStationMapper chargingStationMapper, UserRepository userRepository, LocationStationRepository locationStationRepository) {
        this.chargingStationRepository = chargingStationRepository;
        this.chargingStationMapper = chargingStationMapper;
        this.userRepository = userRepository;
        this.locationStationRepository = locationStationRepository;
    }

    @Transactional
    public ChargingStationResponse createChargingStation (ChargingStationRequest dto) {
        //Convertir l'entité en dto
        ChargingStation chargingStation = chargingStationMapper.convertToEntity(dto);

        //Récupérer l'id du user
       User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + dto.getUserId()));
       //Assigné l'id du user attendu dans le dto
       chargingStation.setUser(user);

        //Récupérer l'id de la locationStation
        LocationStation locationStation = locationStationRepository.findById(dto.getLocationStationId())
                .orElseThrow(()-> new RuntimeException("Adresse de la borne non trouvé avec l'ID " + dto.getLocationStationId()));
        //Assigné l'id de la locationStation attendu dans le dto
        chargingStation.setLocationStation(locationStation);


        ChargingStation newChargingStation = chargingStationRepository.save(chargingStation);

        return chargingStationMapper.ToResponse(newChargingStation);
    }

    public List<ChargingStationResponse> getAllChargingStation() {
        return chargingStationRepository.findAll().stream()
                .map(chargingStationMapper::ToResponse)
                .collect(Collectors.toList());
    }

    public ChargingStationResponse getChargingStationById (Long id) {
        ChargingStation chargingStation = chargingStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borne non trouvé avec l'ID " + id));
        return chargingStationMapper.ToResponse(chargingStation);
    }

    @Transactional
    public ChargingStationResponse updateChargingStation(Long id, ChargingStationRequest request) {
        ChargingStation existingChargingStation = chargingStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borne non trouvé avec l'ID: " + id));
        chargingStationMapper.updateEntityFromDto(request,existingChargingStation);

        ChargingStation updatedChargingStation = chargingStationRepository.save(existingChargingStation);

        return chargingStationMapper.ToResponse(updatedChargingStation);

    }

    @Transactional
    public void deleteChargingStation(Long id) {
        if(!chargingStationRepository.existsById(id)) {
            throw  new RuntimeException("Borne non trouvé avec l'ID: " + id);
        }
        chargingStationRepository.deleteById(id);
    }
}
