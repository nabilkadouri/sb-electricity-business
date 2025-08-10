package com.hb.cda.electricitybusiness.controller.dto;

import com.hb.cda.electricitybusiness.enums.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeslotRequest {

    @NotNull(message = "Le jour de la semaine est obligatoire")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime startTime;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime endTime;

    private Long chargingStationId;

}
