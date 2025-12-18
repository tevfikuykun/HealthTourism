package com.healthtourism.patientriskscoringservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Patient Risk Score (Recovery Score)
 * AI-driven risk scoring combining IoT data, medical history, and procedure complexity
 */
@Entity
@Table(name = "patient_risk_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRiskScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long reservationId;
    
    @Column(nullable = false)
    private Long doctorId;
    
    // Recovery Score (0-100)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal recoveryScore; // 0-100, higher is better
    
    @Column(nullable = false)
    private String scoreCategory; // EXCELLENT (80-100), GOOD (60-79), FAIR (40-59), POOR (0-39)
    
    // Risk Factors
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal iotDataScore; // Based on IoT monitoring data
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal medicalHistoryScore; // Based on medical history
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal procedureComplexityScore; // Based on procedure difficulty
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal complianceScore; // Medication compliance, exercise, etc.
    
    // Trend Analysis
    @Column(nullable = false)
    private String trend; // IMPROVING, STABLE, DECLINING
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal previousScore; // Previous score for trend calculation
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal scoreChange; // Change from previous score
    
    // Alert Status
    @Column(nullable = false)
    private Boolean requiresDoctorAlert; // If score dropped significantly
    
    @Column
    private LocalDateTime lastAlertSentAt;
    
    @Column(length = 2000)
    private String alertReason; // Reason for alert
    
    // AI Explainability (Why did the score change?)
    @Column(length = 5000)
    private String scoreExplanation; // Human-readable explanation of score change
    
    @Column(length = 2000)
    private String contributingFactors; // JSON array of factors that contributed to score
    
    // AI Analysis Details
    @Column(length = 5000)
    private String analysisDetails; // JSON string with detailed analysis
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal aiConfidence; // AI model confidence (0-1)
    
    // Timestamps
    @Column(nullable = false)
    private LocalDateTime calculatedAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (calculatedAt == null) {
            calculatedAt = LocalDateTime.now();
        }
        if (requiresDoctorAlert == null) {
            requiresDoctorAlert = false;
        }
        if (aiConfidence == null) {
            aiConfidence = new BigDecimal("0.85");
        }
    }
}
