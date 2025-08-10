package com.hb.cda.electricitybusiness.business.impl;

import com.hb.cda.electricitybusiness.business.ChargingStationBusiness;
import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.controller.dto.PictureDetailsDTO;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.LocationStation;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.ChargingStationRepository;
import com.hb.cda.electricitybusiness.repository.LocationStationRepository;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import com.hb.cda.electricitybusiness.service.UploadService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ChargingStationBusinessImpl implements ChargingStationBusiness {

    private ChargingStationRepository chargingStationRepository;
    private UserRepository userRepository;
    private LocationStationRepository locationStationRepository;
    private UploadService uploadService;

    public ChargingStationBusinessImpl(ChargingStationRepository chargingStationRepository, UserRepository userRepository, LocationStationRepository locationStationRepository, UploadService uploadService) {
        this.chargingStationRepository = chargingStationRepository;
        this.userRepository = userRepository;
        this.locationStationRepository = locationStationRepository;
        this.uploadService = uploadService;
    }

    @Override
    @Transactional
    public ChargingStation createChargingStation(ChargingStation chargingStation) {

        //Récupérer l'id du user
        User user = userRepository.findById(chargingStation.getUser().getId())
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé avec l'ID: " + chargingStation.getUser().getId()));
        //Assigné l'id du user
        chargingStation.setUser(user);

        //Récupérer l'id de la locationStation
        LocationStation locationStation = locationStationRepository.findById(chargingStation.getLocationStation().getId())
                .orElseThrow(() -> new BusinessException("Adresse de la borne non trouvé avec l'ID: " + chargingStation.getLocationStation().getId()));
        //Assigné l'id de la locationStation
        chargingStation.setLocationStation(locationStation);

        // Sauvegarder la borne de recharge et retourner l'entité sauvegardée
        return chargingStationRepository.save(chargingStation);
    }

    @Override
    public List<ChargingStation> getAllChargingStation() {
        return chargingStationRepository.findAll();
    }

    @Override
    public ChargingStation getChargingStationById(Long id) {
        return chargingStationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Borne de recharge non trouvé avec l'id: " + id));
    }

    @Override
    @Transactional
    public ChargingStation updateChargingStation(Long id, ChargingStation chargingStation) {

        // 1. Trouver la borne de recharge existante
        ChargingStation existingChargingStation = chargingStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borne non trouvée avec l'ID: " + id));

        // 2. Mettre à jour l'entité existante avec les champs de la nouvelle entité
        if (chargingStation.getNameStation() != null) {
            existingChargingStation.setNameStation(chargingStation.getNameStation());
        }
        if (chargingStation.getDescription() != null) {
            existingChargingStation.setDescription(chargingStation.getDescription());
        }
        if (chargingStation.getPower() != null) {
            existingChargingStation.setPower(chargingStation.getPower());
        }
        if (chargingStation.getPricePerHour() != null) {
            existingChargingStation.setPricePerHour(chargingStation.getPricePerHour());
        }
        if (chargingStation.getPicture() != null) {
            existingChargingStation.setPicture(chargingStation.getPicture());
        }
        if (chargingStation.getStatus() != null) {
            existingChargingStation.setStatus(chargingStation.getStatus());
        }
        if (chargingStation.getIsAvailable() != null) {
            existingChargingStation.setIsAvailable(chargingStation.getIsAvailable());
        }
        if (chargingStation.getPlugType() != null) {
            existingChargingStation.setPlugType(chargingStation.getPlugType());
        }


        // 4. Mettre à jour la localisation de la station si nécessaire
        if (chargingStation.getLocationStation() != null) {
            // La borne peut changer de lieu, donc on met à jour la relation
            LocationStation locationStation = locationStationRepository.findById(chargingStation.getLocationStation().getId())
                    .orElseThrow(() -> new RuntimeException("Nouvelle adresse non trouvée avec l'ID: " + chargingStation.getLocationStation().getId()));
            existingChargingStation.setLocationStation(locationStation);
        }

        // 5. Sauvegarder l'entité mise à jour
        return chargingStationRepository.save(existingChargingStation);

    }

    @Override
    @Transactional
    public String updateChargingStationPicture(Long chargingStationId, MultipartFile file, String altText, boolean isMain) {
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

        // Retourner l'URL ou le nom du fichier pour la réponse du contrôleur
        return newFileName;
    }


    @Override
    @Transactional
    public void deleteChargingStation(Long id) {
        ChargingStation chargingStation = chargingStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borne non trouvé avec l'ID: " + id));

        if (chargingStation.getPicture() != null) {
            String currentSrc = chargingStation.getPicture().getSrc();

            if (currentSrc != null && !currentSrc.startsWith("images/default_")) {
                uploadService.removeExisting(currentSrc);
            }
        }

        if(!chargingStationRepository.existsById(id)) {
            throw  new RuntimeException("Borne non trouvé avec l'ID: " + id);
        }
        chargingStationRepository.deleteById(id);
    }

}

