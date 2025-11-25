package com.healthtourism.translationservice.dto;

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
    private String languages;
    private String serviceType;
    private Boolean isCertified;
    private Boolean isAvailableForHospital;
    private Boolean isAvailableForConsultation;
    private BigDecimal pricePerHour;
    private BigDecimal pricePerDocument;
    private String phone;
    private String email;
    private String description;
    private Double rating;
    private Integer totalReviews;
    private Boolean isAvailable;
}

