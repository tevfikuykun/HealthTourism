package com.example.HealthTourism.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPackageDTO {
    private Long id;
    private String packageName;
    private String packageType;
    private Integer durationDays;
    private BigDecimal totalPrice;
    private BigDecimal discountPercentage;
    private BigDecimal finalPrice;
    private Boolean includesFlight;
    private Boolean includesAccommodation;
    private Boolean includesTransfer;
    private Boolean includesCarRental;
    private Boolean includesVisaService;
    private Boolean includesTranslation;
    private Boolean includesInsurance;
    private String description;
    private Double rating;
    private Integer totalReviews;
    private Boolean isActive;
    private Long hospitalId;
    private String hospitalName;
    private Long doctorId;
    private String doctorName;
    private Long accommodationId;
    private String accommodationName;
}

