package com.healthtourism.airecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    private List<DoctorRecommendation> topDoctors;
    private List<PackageRecommendation> topPackages;
    private String reasoning; // AI'nın öneri gerekçesi
    private Double confidenceScore; // Güven skoru (0-1)
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorRecommendation {
        private Long doctorId;
        private String doctorName;
        private String specialization;
        private String hospitalName;
        private Double matchScore; // Eşleşme skoru (0-100)
        private String matchReason; // Eşleşme nedeni
        private BigDecimal estimatedPrice;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageRecommendation {
        private Long packageId;
        private String packageName;
        private String hospitalName;
        private Double matchScore;
        private String matchReason;
        private BigDecimal price;
        private List<String> includedServices;
    }
}
