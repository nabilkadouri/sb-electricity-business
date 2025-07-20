package com.hb.cda.electricitybusiness.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStationResponse {

    private Long id;
    private LocalDateTime createdAt;
    private String nameStation;
    private String description;
    private BigDecimal power;
    private BigDecimal pricePerHour;
    private PictureDetailsDTO picture;
    private ChargingStationStatus status;
    private Boolean isAvailable;
    private String plugType;
    private LocationStationResponse locationStation;
    private List<TimeslotResponse> timeslots;
}
