package com.healthtourism.promotionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionValidationRequest {
    private String code;
    private BigDecimal purchaseAmount;
    private String serviceType;
    private Long serviceId;
}

