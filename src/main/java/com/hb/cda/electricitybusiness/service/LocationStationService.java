package com.hb.cda.electricitybusiness.service;

import com.hb.cda.electricitybusiness.dto.LocationStationRequest;
import com.hb.cda.electricitybusiness.dto.LocationStationResponse;
import com.hb.cda.electricitybusiness.dto.mapper.LocationStationMapper;
import com.hb.cda.electricitybusiness.model.LocationStation;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.LocationStationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationStationService {
    private LocationStationRepository locationStationRepository;
    private LocationStationMapper locationStationMapper;

    public LocationStationService(LocationStationRepository locationStationRepository, LocationStationMapper locationStationMapper) {
        this.locationStationRepository = locationStationRepository;
        this.locationStationMapper = locationStationMapper;
    }

    @Transactional
    public LocationStationResponse createLocationStation(LocationStationRequest dto) {
        //Convertir l'entité en dto
        LocationStation locationStation = locationStationMapper.convertToEntity(dto);

        LocationStation newLocationStation = locationStationRepository.save(locationStation);
        return locationStationMapper.toResponse(newLocationStation);
    }

    public List<LocationStationResponse> getAllLocationStations() {
        return locationStationRepository.findAll().stream()
                .map(locationStationMapper::toResponse)
                .collect(Collectors.toList());
    }

    public LocationStationResponse getLocationStationById(Long id) {
        LocationStation locationStation = locationStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emplacement de station non trouvé avec l'ID: " + id));
        return locationStationMapper.toResponse(locationStation);
    }

    @Transactional
    public LocationStationResponse updateLocationStation(Long id, LocationStationRequest dto) {
        LocationStation existingLocationStation = locationStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emplacement de station non trouvé avec l'ID: " + id));
        locationStationMapper.updateEntityFromDto(dto, existingLocationStation);
        LocationStation updatedLocationStation = locationStationRepository.save(existingLocationStation);
        return locationStationMapper.toResponse(updatedLocationStation);
    }

    @Transactional
    public void deleteLocationStation(Long id) {
        if (!locationStationRepository.existsById(id)) {
            throw new RuntimeException("Emplacement de station non trouvé avec l'ID: " + id);
        }
        locationStationRepository.deleteById(id);
    }
}
