package com.hb.cda.electricitybusiness.service;

import com.hb.cda.electricitybusiness.dto.ChargingStationRequest;
import com.hb.cda.electricitybusiness.dto.ChargingStationResponse;
import com.hb.cda.electricitybusiness.dto.mapper.ChargingStationMapper;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.LocationStation;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.ChargingStationRepository;
import com.hb.cda.electricitybusiness.repository.LocationStationRepository;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChargingStationService {

    private ChargingStationRepository chargingStationRepository;
    private ChargingStationMapper chargingStationMapper;
    private UserRepository userRepository;
    private LocationStationRepository locationStationRepository;
    private UploadService uploadService;

    public ChargingStationService(ChargingStationRepository chargingStationRepository, ChargingStationMapper chargingStationMapper, UserRepository userRepository, LocationStationRepository locationStationRepository, UploadService uploadService) {
        this.chargingStationRepository = chargingStationRepository;
        this.chargingStationMapper = chargingStationMapper;
        this.userRepository = userRepository;
        this.locationStationRepository = locationStationRepository;
        this.uploadService = uploadService;
    }

    @Transactional
    public ChargingStationResponse createChargingStation (ChargingStationRequest dto) {
        //Convertir le dto en entité
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
        ChargingStation chargingStation = chargingStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borne non trouvé avec l'ID: " + id));

        if (chargingStation.getPicture() != null) {
            String currentSrc = chargingStation.getPicture().getSrc();

            if (currentSrc != null && !currentSrc.startsWith("images/default_")) {
                uploadService.removeExisting(currentSrc);
            }
        }

        if(!chargingStationRepository.existsById(id)) {
            throw  new RuntimeException("Borne non trouvé avec l'ID: " + id);
        }
        chargingStationRepository.deleteById(id);
    }
}
