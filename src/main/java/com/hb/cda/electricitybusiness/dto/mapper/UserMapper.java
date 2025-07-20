package com.hb.cda.electricitybusiness.dto.mapper;

import com.hb.cda.electricitybusiness.dto.PictureDetailsDTO;
import com.hb.cda.electricitybusiness.dto.UserResponse;
import com.hb.cda.electricitybusiness.dto.UserUpdateRequest;
import com.hb.cda.electricitybusiness.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {BookingMapper.class, ChargingStationMapper.class})
public interface UserMapper {

    @Mapping(target = "role", source = "roles")
    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    @Mapping(target = "profilePicture", expression = "java(mapPictureDetailsToFullUrl(user.getProfilePicture()))")
    UserResponse userToUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "ownsStation", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "codeCheck", ignore = true)
    @Mapping(target = "chargingStations", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromRequest(UserUpdateRequest userUpdateRequest, @MappingTarget User user);

    // --- Nouvelle méthode pour mapper PictureDetailsDTO en incluant l'URL de base ---
    default PictureDetailsDTO mapPictureDetailsToFullUrl(PictureDetailsDTO pictureDetails) {
        if (pictureDetails == null) {
            return null;
        }

        String src = pictureDetails.getSrc();
        String fullSrc;

        // Construire l'URL complète en fonction du chemin stocké
        // Si le chemin commence par "images/default_", il vient des ressources statiques
        if (src != null && src.startsWith("images/default_")) {
            fullSrc = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/images/") // Chemin de WebConfig pour les images par défaut
                    .path(src.substring(src.lastIndexOf('/') + 1)) // Prend juste le nom du fichier ("default_avatar.png")
                    .toUriString();
        } else {
            // Sinon, c'est une image uploadée qui vient du dossier /uploads
            fullSrc = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/") // Chemin de WebConfig pour les uploads
                    .path(src) // `src` contient déjà le nom de fichier unique ici (UUID.png)
                    .toUriString();
        }

        return new PictureDetailsDTO(pictureDetails.getAlt(), fullSrc, pictureDetails.isMain());
    }
}
