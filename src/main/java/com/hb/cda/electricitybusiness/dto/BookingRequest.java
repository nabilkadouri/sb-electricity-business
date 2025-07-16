package com.hb.cda.electricitybusiness.dto;

import com.hb.cda.electricitybusiness.enums.PaymentMethod;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PaymentMethod paymentType;
    private Long userId;
    private Long chargingStationId;
}
