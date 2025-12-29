package com.hb.cda.electricitybusiness.model;


import com.hb.cda.electricitybusiness.enums.BookingStatus;
import com.hb.cda.electricitybusiness.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "`booking`")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "total_amount", precision = 6, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name="cancel_reason")
    private String cancelReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentMethod paymentType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "charging_station_id", nullable = false)
    private ChargingStation chargingStation;

    public Booking(LocalDateTime startDate, LocalDateTime endDate, PaymentMethod paymentType, BigDecimal totalAmount) {
        this.createdAt = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
        this.startDate = startDate;
        this.endDate = endDate;
        this.paymentType = paymentType;
        this.totalAmount = totalAmount;
    }

    public Booking() {
        this.createdAt = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
    }
}
