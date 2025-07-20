package com.hb.cda.electricitybusiness.controller;

import com.hb.cda.electricitybusiness.dto.ChargingStationRequest;
import com.hb.cda.electricitybusiness.dto.ChargingStationResponse;
import com.hb.cda.electricitybusiness.dto.PictureDetailsDTO;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.repository.ChargingStationRepository;
import com.hb.cda.electricitybusiness.service.ChargingStationService;
import com.hb.cda.electricitybusiness.service.UploadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/charging_stations")
public class ChargingStationController {

    private final ChargingStationRepository chargingStationRepository;
    private ChargingStationService chargingStationService;
    private UploadService uploadService;

    public ChargingStationController(ChargingStationService chargingStationService, UploadService uploadService, ChargingStationRepository chargingStationRepository) {
        this.chargingStationService = chargingStationService;
        this.uploadService = uploadService;
        this.chargingStationRepository = chargingStationRepository;
    }

    @GetMapping
    public ResponseEntity<List<ChargingStationResponse>> getAllChargingStations() {
        List<ChargingStationResponse> chargingStations = chargingStationService.getAllChargingStation();

        return new ResponseEntity<>(chargingStations, HttpStatus.OK);
    }

    @PostMapping("/{chargingStationId}/uploadPicture")
    public ResponseEntity<String> uploadChargingStationPicture(
            @PathVariable Long chargingStationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "alt", required = false) String altText,
            @RequestParam(value = "isMain", defaultValue = "true") boolean isMain
    ) {
        ChargingStation station = chargingStationRepository.findById(chargingStationId)
                .orElseThrow(() -> new RuntimeException("Borne de recharge non trouvée avec l'ID " + chargingStationId));

        // Supprimer l'ancienne image si elle existe et n'est PAS une image par défaut
        if (station.getPicture() != null) {
            String currentSrc = station.getPicture().getSrc();
            if (currentSrc != null && !currentSrc.startsWith("images/default_")) {
                uploadService.removeExisting(currentSrc);
            }
        }

        // Uploader la nouvelle image
        String newFileName = uploadService.uploadImage(file);

        // Mettre à jour l'entité ChargingStation
        PictureDetailsDTO newPictureDetails = new PictureDetailsDTO(altText, newFileName, isMain);
        station.setPicture(newPictureDetails);
        chargingStationRepository.save(station);

        // Construire l'URL pour la réponse
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(newFileName)
                .toUriString();

        return ResponseEntity.ok("Image de la borne de recharge uploadée avec succès pour la station " + chargingStationId + ". URL: " + fileDownloadUri);
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
