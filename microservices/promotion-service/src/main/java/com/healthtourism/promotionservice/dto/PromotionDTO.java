package com.healthtourism.promotionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String discountType;
    private BigDecimal discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxUses;
    private Integer usedCount;
    private BigDecimal minPurchaseAmount;
    private BigDecimal maxDiscountAmount;
    private Boolean isActive;
    private String applicableServiceType;
    private Long specificServiceId;
    private LocalDateTime createdAt;
}

