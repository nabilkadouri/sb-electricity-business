package com.hb.cda.electricitybusiness.model;

import com.hb.cda.electricitybusiness.controller.dto.PictureDetailsDTO;
import com.hb.cda.electricitybusiness.enums.ChargingStationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "`charging_station`")
public class ChargingStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "station_name", length = 255, nullable = false, unique = true)
    private String nameStation;

    @Column(name = "description")
    private String description;

    @Column(name = "power", precision = 5, scale = 2, nullable = false)
    private BigDecimal power;

    @Column(name = "price_per_hour", precision = 6, scale = 2, nullable = false)
    private BigDecimal pricePerHour;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "alt", column = @Column(name = "station_picture_alt", length = 255)),
            @AttributeOverride(name = "src", column = @Column(name = "station_picture_src", length = 255)),
            @AttributeOverride(name = "main", column = @Column(name = "station_picture_is_main"))
    })
    private PictureDetailsDTO picture;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChargingStationStatus status;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Column(name = "plug_type")
    private String plugType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "location_station_id", nullable = false)
    @ToString.Exclude
    private LocationStation locationStation;

    @OneToMany(mappedBy = "chargingStation", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    private List<Timeslot> timeslots = new ArrayList<>();

    @OneToMany(mappedBy = "chargingStation", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    private List<Booking> bookings = new ArrayList<>();

    public ChargingStation() {
        this.createdAt = LocalDateTime.now();
        this.isAvailable = true;
        this.status = ChargingStationStatus.PENDING;
        this.plugType = "Type 2";
        this.picture = new PictureDetailsDTO("Borne de recharge", "images/default_picture_station.png", true);
    }

   public void addTimeslot(Timeslot timeslot) {
        if (timeslots == null) {
            timeslots = new ArrayList<>();
        }
        if (!timeslots.contains(timeslot)) {
            timeslots.add(timeslot);
            timeslot.setChargingStation(this);
        }
    }

    public void removeTimeslot(Timeslot timeslot) {
        if (timeslots != null && timeslots.remove(timeslot)) {
            if (timeslot.getChargingStation() == this) {
                timeslot.setChargingStation(null);
            }
        }
    }

    public void addBooking(Booking booking) {
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        if (!bookings.contains(booking)) {
            bookings.add(booking);
            booking.setChargingStation(this);
        }
    }

    public void removeBooking(Booking booking) {
        if (bookings != null && bookings.remove(booking)) {
            if (booking.getChargingStation() == this) {
                booking.setChargingStation(null);
            }
        }
    }
    @PostLoad
    private void ensureDefaultPicture() {
        if (this.picture == null || this.picture.getSrc() == null) {
            this.picture = new PictureDetailsDTO(
                    "Borne de recharge",
                    "/images/default_picture_station.png",
                    true
            );
        }
    }

}
