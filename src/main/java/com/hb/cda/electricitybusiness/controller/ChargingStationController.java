package com.hb.cda.electricitybusiness.controller;

import com.hb.cda.electricitybusiness.business.ChargingStationBusiness;
import com.hb.cda.electricitybusiness.controller.dto.ChargingStationRequest;
import com.hb.cda.electricitybusiness.controller.dto.ChargingStationResponse;
import com.hb.cda.electricitybusiness.controller.dto.PictureDetailsDTO;
import com.hb.cda.electricitybusiness.controller.dto.mapper.ChargingStationMapper;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.LocationStation;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.service.UploadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/charging_stations")
public class ChargingStationController {

    private final ChargingStationBusiness chargingStationBusiness;
    private final ChargingStationMapper chargingStationMapper;
    private final UploadService uploadService;

    public ChargingStationController(
            ChargingStationBusiness chargingStationBusiness,
            ChargingStationMapper chargingStationMapper,
            UploadService uploadService
    ) {
        this.chargingStationBusiness = chargingStationBusiness;
        this.chargingStationMapper = chargingStationMapper;
        this.uploadService = uploadService;
    }

    @GetMapping
    public ResponseEntity<List<ChargingStationResponse>> getAllChargingStations() {
        List<ChargingStation> chargingStations = chargingStationBusiness.getAllChargingStation();

        List<ChargingStationResponse> chargingStationResponses = chargingStations.stream()
                .map(chargingStationMapper::toResponse)
                .collect(Collectors.toList());

        return new ResponseEntity<>(chargingStationResponses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargingStationResponse> getChargingStationById(@PathVariable Long id) {
        ChargingStation chargingStation = chargingStationBusiness.getChargingStationById(id);
        ChargingStationResponse chargingStationResponse = chargingStationMapper.toResponse(chargingStation);
        return new ResponseEntity<>(chargingStationResponse, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ChargingStationResponse> createChargingStation(@Valid @RequestBody ChargingStationRequest request) {
        ChargingStation chargingStation = chargingStationMapper.convertToEntity(request);

        User user = new User();
        user.setId(request.getUserId());
        chargingStation.setUser(user);

        LocationStation location = new LocationStation();
        location.setId(request.getLocationStationId());
        chargingStation.setLocationStation(location);

        ChargingStation created = chargingStationBusiness.createChargingStation(chargingStation);

        ChargingStationResponse chargingStationResponse = chargingStationMapper.toResponse(created);
        return new ResponseEntity<>(chargingStationResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargingStationResponse> updateChargingStation(
            @PathVariable Long id,
            @Valid @RequestBody ChargingStationRequest request
    ) {
        ChargingStation chargingStation = chargingStationMapper.convertToEntity(request);

        if (request.getUserId() != null) {
            User user = new User();
            user.setId(request.getUserId());
            chargingStation.setUser(user);
        }

        if (request.getLocationStationId() != null) {
            LocationStation location = new LocationStation();
            location.setId(request.getLocationStationId());
            chargingStation.setLocationStation(location);
        }

        ChargingStation updated = chargingStationBusiness.updateChargingStation(id, chargingStation);
        ChargingStationResponse response = chargingStationMapper.toResponse(updated);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-temp-picture")
    public ResponseEntity<PictureDetailsDTO> uploadTempPicture(@RequestParam("file") MultipartFile file, @RequestParam(value = "alt", required = false) String alt) {
        String tempFileName = uploadService.uploadTempImage(file);

        String fileUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/uploads/temp/")
                .path(tempFileName)
                .toUriString();

        PictureDetailsDTO picture = new PictureDetailsDTO(alt != null ? alt : "Photo temporaire de la borne", fileUrl,true
        );

        return ResponseEntity.ok(picture);
    }

    @PostMapping("/{chargingStationId}/uploadPicture")
    public ResponseEntity<PictureDetailsDTO> uploadChargingStationPicture(
            @PathVariable Long chargingStationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "alt", required = false) String altText,
            @RequestParam(value = "isMain", defaultValue = "true") boolean isMain
    ) {
        String newFileName = chargingStationBusiness.updateChargingStationPicture(
                chargingStationId, file, altText, isMain
        );

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(newFileName)
                .toUriString();

        PictureDetailsDTO response = new PictureDetailsDTO();
        response.setSrc(fileDownloadUri);
        response.setAlt(altText);
        response.setMain(isMain);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChargingStation(@PathVariable Long id) {
        chargingStationBusiness.deleteChargingStation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
