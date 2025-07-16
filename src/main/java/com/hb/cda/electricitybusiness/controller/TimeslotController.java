package com.hb.cda.electricitybusiness.controller;


import com.hb.cda.electricitybusiness.dto.TimeslotRequest;
import com.hb.cda.electricitybusiness.dto.TimeslotResponse;
import com.hb.cda.electricitybusiness.service.TimeslotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
public class TimeslotController {
    private TimeslotService timeslotService;

    public TimeslotController(TimeslotService timeslotService) {
        this.timeslotService = timeslotService;
    }

    @PostMapping
    public ResponseEntity<TimeslotResponse> createTimeslot(@Valid @RequestBody TimeslotRequest request) {
        TimeslotResponse newTimeslot = timeslotService.createTimeslot(request);
        return new ResponseEntity<>(newTimeslot, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TimeslotResponse>> getAllTimeslots() {
        List<TimeslotResponse> timeslots = timeslotService.getAllTimeslots();
        return new ResponseEntity<>(timeslots, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeslotResponse> getTimeslotById(@PathVariable Long id) {
        TimeslotResponse timeslot = timeslotService.getTimeslotById(id);
        return new ResponseEntity<>(timeslot, HttpStatus.OK);
    }

    @GetMapping("/by-charging-station/{chargingStationId}/available")
    public ResponseEntity<List<TimeslotResponse>> getAvailableTimeslotsByChargingStation(@PathVariable Long chargingStationId) {
        List<TimeslotResponse> timeslots = timeslotService.getAvailableTimeslotsByChargingStation(chargingStationId);
        return new ResponseEntity<>(timeslots, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeslotResponse> updateTimeslot(@PathVariable Long id, @Valid @RequestBody TimeslotRequest request) {
        TimeslotResponse updatedTimeslot = timeslotService.updateTimeslot(id, request);
        return new ResponseEntity<>(updatedTimeslot, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeslot(@PathVariable Long id) {
        timeslotService.deleteTimeslot(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
