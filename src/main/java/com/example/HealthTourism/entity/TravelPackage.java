package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "travel_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String packageName;

    @Column(nullable = false)
    private String packageType; // Basic, Standard, Premium, VIP

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private BigDecimal discountPercentage; // İndirim yüzdesi

    @Column(nullable = false)
    private BigDecimal finalPrice; // İndirim sonrası fiyat

    @Column(nullable = false)
    private Boolean includesFlight;

    @Column(nullable = false)
    private Boolean includesAccommodation;

    @Column(nullable = false)
    private Boolean includesTransfer;

    @Column(nullable = false)
    private Boolean includesCarRental;

    @Column(nullable = false)
    private Boolean includesVisaService;

    @Column(nullable = false)
    private Boolean includesTranslation;

    @Column(nullable = false)
    private Boolean includesInsurance;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private Integer totalReviews;

    @Column(nullable = false)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;
}

