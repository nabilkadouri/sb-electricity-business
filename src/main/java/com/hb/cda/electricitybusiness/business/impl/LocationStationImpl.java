package com.hb.cda.electricitybusiness.business.impl;

import com.hb.cda.electricitybusiness.business.LocationStationBusiness;
import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.model.LocationStation;
import com.hb.cda.electricitybusiness.repository.LocationStationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationStationImpl implements LocationStationBusiness {

    private LocationStationRepository locationStationRepository;

    public LocationStationImpl(LocationStationRepository locationStationRepository) {
        this.locationStationRepository = locationStationRepository;
    }

    @Override
    @Transactional
    public LocationStation createLocationStation(LocationStation locationStation) {
        return locationStationRepository.save(locationStation);
    }

    @Override
    public List<LocationStation> getAllLocationStation() {
        return locationStationRepository.findAll();
    }

    @Override
    public LocationStation getLocationStationById(Long id) {
        return locationStationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Adresse de la borne non trouvé avec l'id: " + id));
    }

    @Override
    @Transactional
    public LocationStation updateLocationStation(Long id, LocationStation locationStation) {
        LocationStation existingLocationStation = locationStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emplacement de station non trouvé avec l'ID: " + id));
        existingLocationStation.setAddress(locationStation.getAddress());
        existingLocationStation.setPostaleCode(locationStation.getPostaleCode());
        existingLocationStation.setCity(locationStation.getCity());
        existingLocationStation.setLatitude(locationStation.getLatitude());
        existingLocationStation.setLongitude(locationStation.getLongitude());

        return locationStationRepository.save(locationStation);
    }

    @Override
    @Transactional
    public void deleteLocationsStation(Long id) {
        if (!locationStationRepository.existsById(id)) {
            throw new BusinessException("Adresse de la borne non trouvé avec l'ID: " + id);
        }
        locationStationRepository.deleteById(id);
    }
}
