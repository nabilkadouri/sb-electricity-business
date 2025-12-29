package com.hb.cda.electricitybusiness.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusUpdateRequest {
    private String status;
    private String cancelReason;
}
