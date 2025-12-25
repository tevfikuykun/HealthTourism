package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flight_reservations", indexes = {
    @Index(name = "idx_reservation_number", columnList = "reservation_number"),
    @Index(name = "idx_pnr_code", columnList = "pnr_code"),
    @Index(name = "idx_user", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_flight_booking", columnList = "flight_booking_id"),
    // Composite indexes for common query patterns
    @Index(name = "idx_user_status", columnList = "user_id,status"),
    @Index(name = "idx_status_created", columnList = "status,created_at"),
    @Index(name = "idx_created_at_status", columnList = "created_at,status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reservationNumber;
    
    /**
     * PNR (Passenger Name Record) kodu - Havayolu şirketinden gelen benzersiz kod.
     * Müşteri hizmetleri ve havalimanı transfer ekibi için hayati öneme sahiptir.
     */
    @Column(name = "pnr_code", unique = true)
    private String pnrCode;

    @Column(nullable = false)
    private Integer numberOfPassengers;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_booking_id", nullable = false)
    private FlightBooking flightBooking;
}

