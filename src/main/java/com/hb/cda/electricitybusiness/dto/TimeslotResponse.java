package com.hb.cda.electricitybusiness.dto;

import com.hb.cda.electricitybusiness.enums.DayOfWeek;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeslotResponse {
    private Long id;
    private DayOfWeek dayOfWeek;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isAvailable;
    private Long chargingStationId;

}
