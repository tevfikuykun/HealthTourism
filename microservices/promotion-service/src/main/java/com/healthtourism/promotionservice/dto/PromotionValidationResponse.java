package com.healthtourism.promotionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionValidationResponse {
    private Boolean isValid;
    private String message;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private PromotionDTO promotion;
}

