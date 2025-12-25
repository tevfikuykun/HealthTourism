package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "transfer_services", indexes = {
    @Index(name = "idx_company_name", columnList = "company_name"),
    @Index(name = "idx_vehicle_type", columnList = "vehicle_type"),
    @Index(name = "idx_service_type", columnList = "service_type"),
    @Index(name = "idx_pickup_location", columnList = "pickup_location"),
    @Index(name = "idx_dropoff_location", columnList = "dropoff_location"),
    @Index(name = "idx_available", columnList = "is_available"),
    @Index(name = "idx_rating", columnList = "rating"),
    @Index(name = "idx_price", columnList = "price"),
    @Index(name = "idx_meet_greet", columnList = "has_meet_and_greet"),
    // Composite indexes for common query patterns
    @Index(name = "idx_pickup_dropoff_available", columnList = "pickup_location,dropoff_location,is_available"),
    @Index(name = "idx_type_capacity_available", columnList = "vehicle_type,passenger_capacity,is_available"),
    @Index(name = "idx_price_available", columnList = "price,is_available"),
    @Index(name = "idx_rating_available", columnList = "rating,is_available")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String vehicleType; // Sedan, SUV, Van, Minibus, Bus

    @Column(nullable = false)
    private Integer passengerCapacity;

    @Column(nullable = false)
    private String serviceType; // Airport-Hospital, Airport-Hotel, Hotel-Hospital

    @Column(nullable = false)
    private String pickupLocation; // Airport, Hotel, Hospital

    @Column(nullable = false)
    private String dropoffLocation;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private Double distanceKm;

    @Column(nullable = false)
    private Boolean hasMeetAndGreet; // Karşılama hizmeti

    @Column(nullable = false)
    private Boolean hasLuggageAssistance;

    @Column(nullable = false)
    private Boolean hasWifi;

    @Column(nullable = false)
    private Boolean hasChildSeat;

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
}

