package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "translation_services", indexes = {
    @Index(name = "idx_translator_name", columnList = "translator_name"),
    @Index(name = "idx_service_type", columnList = "service_type"),
    @Index(name = "idx_is_certified", columnList = "is_certified"),
    @Index(name = "idx_is_available", columnList = "is_available"),
    @Index(name = "idx_rating", columnList = "rating"),
    @Index(name = "idx_price_per_hour", columnList = "price_per_hour"),
    @Index(name = "idx_hospital_available", columnList = "is_available_for_hospital"),
    @Index(name = "idx_consultation_available", columnList = "is_available_for_consultation"),
    // Composite indexes for common query patterns
    @Index(name = "idx_certified_available", columnList = "is_certified,is_available"),
    @Index(name = "idx_type_certified_available", columnList = "service_type,is_certified,is_available"),
    @Index(name = "idx_rating_available", columnList = "rating,is_available"),
    @Index(name = "idx_hospital_certified_available", columnList = "is_available_for_hospital,is_certified,is_available")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String translatorName;

    @Column(nullable = false)
    private String languages; // Çeviri yapılan diller (virgülle ayrılmış)

    @Column(nullable = false)
    private String serviceType; // Medical, Legal, General, Document

    @Column(nullable = false)
    private Boolean isCertified; // Sertifikalı tercüman

    @Column(nullable = false)
    private Boolean isAvailableForHospital; // Hastanede tercümanlık

    @Column(nullable = false)
    private Boolean isAvailableForConsultation; // Muayene tercümanlığı

    @Column(nullable = false)
    private BigDecimal pricePerHour;

    @Column(nullable = false)
    private BigDecimal pricePerDocument;

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

