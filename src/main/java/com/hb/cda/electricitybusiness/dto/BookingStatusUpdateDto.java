package com.hb.cda.electricitybusiness.dto;

import com.hb.cda.electricitybusiness.enums.BookingStatus;
import lombok.Data;

@Data
public class BookingStatusUpdateDto {
    private BookingStatus status;
}
