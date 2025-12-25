package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "car_rental_reservations", indexes = {
    @Index(name = "idx_car_rental", columnList = "car_rental_id"),
    @Index(name = "idx_user", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status"),
    // Composite index for date conflict checking (critical for preventing double booking)
    @Index(name = "idx_car_dates_status", columnList = "car_rental_id,pickup_date,dropoff_date,status"),
    // Index for date range queries
    @Index(name = "idx_dates", columnList = "pickup_date,dropoff_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarRentalReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reservationNumber;

    @Column(nullable = false)
    private LocalDate pickupDate;

    @Column(nullable = false)
    private LocalDate dropoffDate;

    @Column(nullable = false)
    private Integer rentalDays;

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
    @JoinColumn(name = "car_rental_id", nullable = false)
    private CarRental carRental;
}

