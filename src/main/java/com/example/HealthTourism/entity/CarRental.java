package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "car_rentals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarRental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String carModel;

    @Column(nullable = false)
    private String carType; // Economy, Standard, Luxury, SUV, Van

    @Column(nullable = false)
    private Integer passengerCapacity;

    @Column(nullable = false)
    private Integer luggageCapacity;

    @Column(nullable = false)
    private String transmission; // Manual, Automatic

    @Column(nullable = false)
    private Boolean hasAirConditioning;

    @Column(nullable = false)
    private Boolean hasGPS;

    @Column(nullable = false)
    private BigDecimal pricePerDay;

    @Column(nullable = false)
    private BigDecimal pricePerWeek;

    @Column(nullable = false)
    private BigDecimal pricePerMonth;

    @Column(nullable = false)
    private String pickupLocation;

    @Column(nullable = false)
    private String dropoffLocation;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private Integer totalReviews;

    @Column(nullable = false)
    private Boolean isAvailable;

    @Column(nullable = false)
    private Boolean includesInsurance;

    @Column(nullable = false)
    private Boolean includesDriver; // Şoförlü kiralama seçeneği
}

