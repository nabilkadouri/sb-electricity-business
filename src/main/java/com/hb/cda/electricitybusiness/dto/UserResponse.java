package com.hb.cda.electricitybusiness.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String firstName;
    private String address;
    private String postaleCode;
    private String city;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private Boolean ownsStation;
    private PictureDetailsDTO profilePicture;
    private String role;
    private List<BookingResponse> bookings;
    private List<ChargingStationResponse> chargingStations;
}
