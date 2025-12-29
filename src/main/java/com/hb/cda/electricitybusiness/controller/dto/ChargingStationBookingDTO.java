package com.hb.cda.electricitybusiness.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStationBookingDTO {
    private Long id;
    private String nameStation;
    private BigDecimal power;
    private String plugType;
}