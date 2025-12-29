package com.hb.cda.electricitybusiness.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBookingDTO {
    private String name;
    private String firstName;
    private PictureDetailsDTO profilePicture;
}
