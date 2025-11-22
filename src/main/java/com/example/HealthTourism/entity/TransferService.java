package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "transfer_services")
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

