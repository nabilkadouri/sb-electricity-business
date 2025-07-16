package com.hb.cda.electricitybusiness.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "location_station")
public class LocationStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location-name", length = 255)
    private String locationName;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @Column(name = "postale_code", length = 10, nullable = false)
    private String postaleCode;

    @Column(name = "city", length = 255, nullable = false)
    private String city;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @OneToMany(mappedBy = "locationStation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChargingStation> chargingStations = new ArrayList<>();
}
