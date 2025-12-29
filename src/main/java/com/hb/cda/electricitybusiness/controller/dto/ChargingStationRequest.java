package com.hb.cda.electricitybusiness.controller.dto;

import com.hb.cda.electricitybusiness.enums.ChargingStationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStationRequest {
    @NotBlank(message = "Le nom de la borne est obligatoire")
    private String nameStation;
    private String description;
    @NotNull(message = "La capacité de puissance est obligatoire")
    @Positive(message = "La capacité de puissance doit être positive")
    private BigDecimal power;
    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positive")
    private BigDecimal pricePerHour;
    private PictureDetailsDTO picture;
    private ChargingStationStatus status;
    private Boolean isAvailable;
    @NotNull(message = "L'ID de l'emplacement de la station est obligatoire")
    private Long locationStationId;
    @NotNull(message = "L'ID du propriétaire est obligatoire")
    private Long userId;
}
