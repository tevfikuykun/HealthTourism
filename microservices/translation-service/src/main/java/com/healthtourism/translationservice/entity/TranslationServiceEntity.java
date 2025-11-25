package com.healthtourism.translationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "translation_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationServiceEntity {
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

