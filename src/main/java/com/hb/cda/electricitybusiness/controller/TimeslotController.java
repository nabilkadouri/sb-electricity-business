package com.hb.cda.electricitybusiness.controller;


import com.hb.cda.electricitybusiness.business.TimeslotBusiness;
import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.dto.TimeslotRequest;
import com.hb.cda.electricitybusiness.dto.TimeslotResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
public class TimeslotController {
    private TimeslotBusiness timeslotBusiness;

    public TimeslotController(TimeslotBusiness timeslotBusiness) {
        this.timeslotBusiness = timeslotBusiness;
    }

    @GetMapping
    public ResponseEntity<List<TimeslotResponse>> getAllTimeslots() {
        List<TimeslotResponse> timeslots = timeslotBusiness.getAllTimeslots();
        return new ResponseEntity<>(timeslots, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeslotResponse> getTimeslotById(@PathVariable Long id) {
        try {
            TimeslotResponse timeslot = timeslotBusiness.getTimeslotById(id);
            return new ResponseEntity<>(timeslot, HttpStatus.OK);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/by-charging-station/{chargingStationId}/available")
    public ResponseEntity<List<TimeslotResponse>> getAvailableTimeslotsByChargingStation(@PathVariable Long chargingStationId) {
        List<TimeslotResponse> timeslots = timeslotBusiness.getAvailableTimeslotsByChargingStation(chargingStationId);
        return new ResponseEntity<>(timeslots, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TimeslotResponse> createTimeslot(@Valid @RequestBody TimeslotRequest request) {
        try {
            TimeslotResponse newTimeslot = timeslotBusiness.createTimeslot(request);
            return new ResponseEntity<>(newTimeslot, HttpStatus.CREATED);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<List<TimeslotResponse>> createMultipleTimeslots(
            @Valid @RequestBody List<TimeslotRequest> timeslotRequests) {
        try {
            List<TimeslotResponse> createdTimeslots = timeslotBusiness.createMultipleTimeslots(timeslotRequests);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTimeslots);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeslotResponse> updateTimeslot(@PathVariable Long id, @Valid @RequestBody TimeslotRequest request) {
        try {
            TimeslotResponse updatedTimeslot = timeslotBusiness.updateTimeslot(id, request);
            return new ResponseEntity<>(updatedTimeslot, HttpStatus.OK);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeslot(@PathVariable Long id) {
        try {
            timeslotBusiness.deleteTimeslot(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
