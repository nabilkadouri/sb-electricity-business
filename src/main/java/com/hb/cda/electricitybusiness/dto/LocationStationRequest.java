package com.hb.cda.electricitybusiness.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationStationRequest {
    @NotBlank(message = "Le nom de l'emplacement ne peut pas être vide")
    @Size(max = 255, message = "Le nom de l'emplacement ne peut pas dépasser 255 caractères")
    private String locationName;

    @NotBlank(message = "L'adresse ne peut pas être vide")
    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    private String address;

    @NotBlank(message = "Le code postal ne peut pas être vide")
    @Size(max = 10, message = "Le code postal ne peut pas dépasser 10 caractères")
    private String postaleCode;

    @NotBlank(message = "La ville ne peut pas être vide")
    @Size(max = 255, message = "La ville ne peut pas dépasser 255 caractères")
    private String city;

    private Double latitude;

    private Double longitude;
}
