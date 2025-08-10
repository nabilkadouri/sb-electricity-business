package com.hb.cda.electricitybusiness.controller;

import com.hb.cda.electricitybusiness.business.LocationStationBusiness;
import com.hb.cda.electricitybusiness.controller.dto.LocationStationRequest;
import com.hb.cda.electricitybusiness.controller.dto.LocationStationResponse;
import com.hb.cda.electricitybusiness.controller.dto.mapper.LocationStationMapper;
import com.hb.cda.electricitybusiness.model.LocationStation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/location_stations")
public class LocationStationController {

    private final LocationStationMapper locationStationMapper;
    private LocationStationBusiness locationStationBusiness;

    public LocationStationController(LocationStationBusiness locationStationBusiness, LocationStationMapper locationStationMapper) {
        this.locationStationBusiness = locationStationBusiness;
        this.locationStationMapper = locationStationMapper;
    }

    @GetMapping
    public ResponseEntity<List<LocationStationResponse>> getAllLocationStation(){

        List<LocationStation> locationStations = locationStationBusiness.getAllLocationStation();

        List<LocationStationResponse> locationStationsResponse = locationStations.stream()
                .map(locationStationMapper::toResponse)
                .collect(Collectors.toList());

        return new ResponseEntity<>(locationStationsResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<LocationStationResponse> getLocationStationById(@PathVariable Long id) {
        LocationStation locationStation = locationStationBusiness.getLocationStationById(id);

        LocationStationResponse locationStationResponse = locationStationMapper.toResponse(locationStation);

        return new ResponseEntity<>(locationStationResponse, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LocationStationResponse> createLocationStation(@Valid @RequestBody LocationStationRequest request) {

        LocationStation locationStation = locationStationMapper.convertToEntity(request);

        LocationStation newLocationStation = locationStationBusiness.createLocationStation(locationStation);

        LocationStationResponse locationStationResponse = locationStationMapper.toResponse(newLocationStation);

        return new ResponseEntity<>(locationStationResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationStationResponse> updateLocationStation(@PathVariable Long id, @Valid @RequestBody LocationStationRequest request) {

        LocationStation updateLocationStation = locationStationMapper.convertToEntity(request);

        LocationStation resultEntity = locationStationBusiness.updateLocationStation(id, updateLocationStation);

        LocationStationResponse response = locationStationMapper.toResponse(resultEntity);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocationStation(@PathVariable Long id) {
        locationStationBusiness.deleteLocationsStation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
