package com.hb.cda.electricitybusiness.controller.dto;

import com.hb.cda.electricitybusiness.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    @NotNull(message = "La date de début est obligatoire")
    private LocalDateTime startDate;
    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime endDate;
    private PaymentMethod paymentType;
    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;
    @NotNull(message = "L'ID de la borne de recharge est obligatoire")
    private Long chargingStationId;
}
