package com.hb.cda.electricitybusiness.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Data
public class TimeslotRequest {

    @NotNull(message = "Le jour de la semaine est obligatoire")
    private String dayOfWeek;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalDateTime startTime;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalDateTime endTime;

    @NotNull(message = "L'ID de la borne de recharge est obligatoire")
    private Long chargingStationId;

}
