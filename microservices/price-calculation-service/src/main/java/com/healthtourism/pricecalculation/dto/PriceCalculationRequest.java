package com.healthtourism.pricecalculation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculationRequest {
    private Long hospitalId;
    private Long doctorId;
    private Long treatmentId;
    private BigDecimal hospitalPrice;
    private BigDecimal doctorPrice;
    private BigDecimal treatmentPrice;
    
    // Flight information
    private Long flightId;
    private BigDecimal flightPrice;
    
    // Accommodation information
    private Long accommodationId;
    private Integer accommodationNights;
    private BigDecimal accommodationPricePerNight;
    
    // Additional services
    private List<AdditionalService> additionalServices;
    
    // Transfer information
    private Boolean vipTransfer;
    private BigDecimal transferPrice;
    
    // Translation service
    private Boolean translationService;
    private BigDecimal translationPrice;
    
    // Insurance
    private Boolean insurance;
    private BigDecimal insurancePrice;
    
    // Currency
    private String currency = "TRY";
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalService {
        private String serviceType; // TRANSLATOR, VIP_TRANSFER, INSURANCE, etc.
        private BigDecimal price;
        private Integer quantity = 1;
    }
}
