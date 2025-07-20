package com.hb.cda.electricitybusiness.dto;

import com.hb.cda.electricitybusiness.dto.mapper.ChargingStationMapper;
import com.hb.cda.electricitybusiness.enums.BookingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponse {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private Long userId;
    private Long chargingStationId;
}
