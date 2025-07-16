package com.hb.cda.electricitybusiness.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationStationResponse {
    private Long id;
    private String locationName;
    private String address;
    private String postaleCode;
    private String city;
    private Double latitude;
    private Double longitude;
    //private List<ChargingstationResponse> chargingStations;
}
