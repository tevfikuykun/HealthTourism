package com.healthtourism.pricecalculation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculationResponse {
    private BigDecimal basePrice;
    private BigDecimal flightPrice;
    private BigDecimal accommodationPrice;
    private BigDecimal additionalServicesPrice;
    private BigDecimal transferPrice;
    private BigDecimal translationPrice;
    private BigDecimal insurancePrice;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;
    private String currency;
    
    private List<PriceBreakdown> breakdown;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceBreakdown {
        private String item;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal total;
    }
}
