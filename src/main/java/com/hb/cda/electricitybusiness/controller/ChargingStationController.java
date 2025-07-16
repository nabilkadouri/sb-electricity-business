package com.hb.cda.electricitybusiness.controller;

import com.hb.cda.electricitybusiness.dto.ChargingStationRequest;
import com.hb.cda.electricitybusiness.dto.ChargingStationResponse;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.service.ChargingStationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charging_stations")
public class ChargingStationController {

    private ChargingStationService chargingStationService;

    public ChargingStationController(ChargingStationService chargingStationService) {
        this.chargingStationService = chargingStationService;
    }

    @GetMapping
    public ResponseEntity<List<ChargingStationResponse>> getAllChargingStations() {
        List<ChargingStationResponse> chargingStations = chargingStationService.getAllChargingStation();

        return new ResponseEntity<>(chargingStations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargingStationResponse> getChargingStationById(@PathVariable Long id) {
        ChargingStationResponse chargingStation = chargingStationService.getChargingStationById(id);
        return new ResponseEntity<>(chargingStation, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ChargingStationResponse> createChargingStation(@Valid @RequestBody ChargingStationRequest request) {
        ChargingStationResponse newChargingStation = chargingStationService.createChargingStation(request);
        return new ResponseEntity<>(newChargingStation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargingStationResponse> updateChargingStation(@PathVariable Long id, @Valid @RequestBody ChargingStationRequest request) {
        ChargingStationResponse updatedChargingStation = chargingStationService.updateChargingStation(id, request);
        return new ResponseEntity<>(updatedChargingStation, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteChargingStation(@PathVariable Long id) {
        chargingStationService.deleteChargingStation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
