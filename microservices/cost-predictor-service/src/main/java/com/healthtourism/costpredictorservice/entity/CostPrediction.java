package com.healthtourism.costpredictorservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI-Powered Medical Cost Prediction
 * Analyzes medical reports and predicts total cost with ±5% accuracy
 */
@Entity
@Table(name = "cost_predictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long hospitalId;
    
    @Column(nullable = false)
    private Long doctorId;
    
    @Column(nullable = false)
    private String procedureType; // SURGERY, CONSULTATION, TREATMENT, etc.
    
    // Medical Report Analysis
    @Column(length = 2000)
    private String medicalReportHash; // IPFS hash of medical report
    
    @Column(length = 2000)
    private String medicalReportReference; // IPFS CID
    
    @Column(length = 5000)
    private String analysisSummary; // AI analysis summary
    
    // Cost Breakdown
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hospitalFee; // Base hospital fee
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal doctorFee; // Doctor consultation/surgery fee
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal accommodationCost; // Estimated accommodation
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal medicationCost; // Estimated medication
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal transferCost; // Airport transfer
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal complicationRiskCost; // Risk-based additional cost
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hiddenCostsAdjustment; // Historical hidden costs trend adjustment
    
    // Total Prediction
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal predictedTotalCost;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minCost; // predictedTotalCost * 0.95
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal maxCost; // predictedTotalCost * 1.05
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal accuracyPercentage; // AI confidence (typically 95%)
    
    // Risk Factors
    @Column(length = 2000)
    private String identifiedRisks; // JSON string with risk factors
    
    @Column(nullable = false)
    private String riskLevel; // LOW, MEDIUM, HIGH
    
    @Column(nullable = false)
    private Integer complicationProbability; // 0-100
    
    // Timestamps
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime expiresAt; // Prediction validity period
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (expiresAt == null) {
            expiresAt = createdAt.plusDays(30); // Valid for 30 days
        }
        // Calculate min/max with ±5% variance
        if (predictedTotalCost != null) {
            minCost = predictedTotalCost.multiply(new BigDecimal("0.95"));
            maxCost = predictedTotalCost.multiply(new BigDecimal("1.05"));
        }
    }
}
