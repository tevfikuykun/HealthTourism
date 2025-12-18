package com.healthtourism.insuranceservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Smart Medical Travel Insurance Policy
 * Blockchain-backed insurance policy for medical tourism
 */
@Entity
@Table(name = "insurance_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long insuranceId;
    
    @Column(nullable = false)
    private Long reservationId; // Associated reservation
    
    @Column(nullable = false, unique = true)
    private String policyNumber; // Format: HT-INS-YYYYMM-XXXX
    
    @Column(nullable = false)
    private String status; // ACTIVE, EXPIRED, CLAIMED, CANCELLED
    
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    @Column(nullable = false)
    private LocalDateTime endDate;
    
    @Column(nullable = false)
    private BigDecimal coverageAmount;
    
    @Column(nullable = false)
    private BigDecimal premium;
    
    // Blockchain Integration
    @Column(length = 500)
    private String blockchainHash; // SHA-256 hash stored on blockchain
    
    @Column(length = 1000)
    private String blockchainReference; // IPFS CID or blockchain transaction ID
    
    @Column(nullable = false)
    private Boolean isBlockchainVerified; // Whether policy is verified on blockchain
    
    // Coverage Details
    @Column(length = 2000)
    private String coverageDetails; // JSON string with coverage breakdown
    
    @Column(length = 2000)
    private String exclusions; // JSON string with exclusions
    
    // Post-Op Complications Coverage
    @Column(nullable = false)
    private Boolean coversPostOpComplications;
    
    @Column(nullable = false)
    private Boolean coversEmergencyEvacuation;
    
    @Column(nullable = false)
    private Boolean coversRepatriation;
    
    @Column(nullable = false)
    private Boolean coversFollowUpCare;
    
    // Timestamps
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (policyNumber == null || policyNumber.isEmpty()) {
            policyNumber = generatePolicyNumber();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generatePolicyNumber() {
        String yearMonth = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
        long randomNum = (long) (Math.random() * 10000);
        return String.format("HT-INS-%s-%04d", yearMonth, randomNum);
    }
}
