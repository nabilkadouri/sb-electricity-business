package com.hb.cda.electricitybusiness.controller;

import com.hb.cda.electricitybusiness.business.ChargingStationBusiness;
import com.hb.cda.electricitybusiness.controller.dto.ChargingStationRequest;
import com.hb.cda.electricitybusiness.controller.dto.ChargingStationResponse;
import com.hb.cda.electricitybusiness.controller.dto.PictureDetailsDTO;
import com.hb.cda.electricitybusiness.controller.dto.mapper.ChargingStationMapper;
import com.hb.cda.electricitybusiness.model.ChargingStation;
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

    private ChargingStationBusiness chargingStationBusiness;
    private ChargingStationMapper chargingStationMapper;

    public ChargingStationController(ChargingStationBusiness chargingStationBusiness, ChargingStationMapper chargingStationMapper) {
        this.chargingStationBusiness = chargingStationBusiness;
        this.chargingStationMapper = chargingStationMapper;
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

        ChargingStation newChargingStation = chargingStationBusiness.createChargingStation(chargingStation);

        ChargingStationResponse chargingStationResponse = chargingStationMapper.toResponse(newChargingStation);
        return new ResponseEntity<>(chargingStationResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargingStationResponse> updateChargingStation(@PathVariable Long id, @Valid @RequestBody ChargingStationRequest request) {

        ChargingStation  updatedChargingStation = chargingStationMapper.convertToEntity(request);

        ChargingStation resultEntity = chargingStationBusiness.updateChargingStation(id, updatedChargingStation);

        ChargingStationResponse response = chargingStationMapper.toResponse(resultEntity);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{chargingStationId}/uploadPicture")
    public ResponseEntity<String> uploadChargingStationPicture(
            @PathVariable Long chargingStationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "alt", required = false) String altText,
            @RequestParam(value = "isMain", defaultValue = "true") boolean isMain
    ) {

        // Le contrôleur appelle simplement le service et récupère le nom du fichier
        String newFileName = chargingStationBusiness.updateChargingStationPicture(chargingStationId, file, altText, isMain);

        // Construire l'URL pour la réponse du contrôleur
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(newFileName)
                .toUriString();

        return ResponseEntity.ok("Image de la borne de recharge uploadée avec succès pour la station " + chargingStationId + ". URL: " + fileDownloadUri);

    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteChargingStation(@PathVariable Long id) {
        chargingStationBusiness.deleteChargingStation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
