package com.hb.cda.electricitybusiness.controller;

import com.hb.cda.electricitybusiness.dto.LocationStationRequest;
import com.hb.cda.electricitybusiness.dto.LocationStationResponse;
import com.hb.cda.electricitybusiness.service.LocationStationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location_stations")
public class LocationStationController {

    private LocationStationService locationStationService;

    public LocationStationController(LocationStationService locationStationService) {
        this.locationStationService = locationStationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationStationResponse>> getAllLocationStation(){
        List<LocationStationResponse> locationStations = locationStationService.getAllLocationStations();

        return new ResponseEntity<>(locationStations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<LocationStationResponse> getLocationStationById(Long id) {
        LocationStationResponse locationStation = locationStationService.getLocationStationById(id);

        return new ResponseEntity<>(locationStation, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LocationStationResponse> createLocationStation(@Valid @RequestBody LocationStationRequest request) {
        LocationStationResponse newLocationStation = locationStationService.createLocationStation(request);
        return new ResponseEntity<>(newLocationStation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationStationResponse> updateLocationStation(@PathVariable Long id, @Valid @RequestBody LocationStationRequest request) {
        LocationStationResponse updatedLocationStation = locationStationService.updateLocationStation(id, request);
        return new ResponseEntity<>(updatedLocationStation, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocationStation(@PathVariable Long id) {
        locationStationService.deleteLocationStation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
