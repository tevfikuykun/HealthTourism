package com.healthtourism.insuranceservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceDTO {
    private Long id;
    private String companyName;
    private String insuranceType;
    private String coverageArea;
    private BigDecimal coverageAmount;
    private BigDecimal premium;
    private Integer validityDays;
    private String coverageDetails;
    private String exclusions;
    private String phone;
    private String email;
    private String description;
    private Double rating;
    private Integer totalReviews;
    private Boolean isActive;
}

