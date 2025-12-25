package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "travel_packages", indexes = {
    @Index(name = "idx_package_name", columnList = "package_name"),
    @Index(name = "idx_package_type", columnList = "package_type"),
    @Index(name = "idx_hospital", columnList = "hospital_id"),
    @Index(name = "idx_doctor", columnList = "doctor_id"),
    @Index(name = "idx_accommodation", columnList = "accommodation_id"),
    @Index(name = "idx_is_active", columnList = "is_active"),
    @Index(name = "idx_rating", columnList = "rating"),
    @Index(name = "idx_final_price", columnList = "final_price"),
    @Index(name = "idx_includes_flight", columnList = "includes_flight"),
    // Composite indexes for common query patterns
    @Index(name = "idx_type_active", columnList = "package_type,is_active"),
    @Index(name = "idx_hospital_active", columnList = "hospital_id,is_active"),
    @Index(name = "idx_type_flight_active", columnList = "package_type,includes_flight,is_active"),
    @Index(name = "idx_price_active", columnList = "final_price,is_active"),
    @Index(name = "idx_rating_active", columnList = "rating,is_active")
})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;
}

