package com.example.HealthTourism.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationServiceDTO {
    private Long id;
    private String translatorName;
    private String languages; // Çeviri yapılan diller (virgülle ayrılmış)
    private String serviceType; // Medical, Legal, General, Document
    private Boolean isCertified; // Sertifikalı tercüman
    private Boolean isAvailableForHospital; // Hastanede tercümanlık
    private Boolean isAvailableForConsultation; // Muayene tercümanlığı
    private BigDecimal pricePerHour;
    private BigDecimal pricePerDocument;
    private String phone;
    private String email;
    private String description;
    private Double rating;
    private Integer totalReviews;
    private Boolean isAvailable;
}

