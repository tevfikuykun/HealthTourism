package com.healthtourism.gamificationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Health Token - Blockchain-backed reward token
 * Earned through rehabilitation activities, medication compliance, and healthy lifestyle
 */
@Entity
@Table(name = "health_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long reservationId; // Associated reservation
    
    @Column(nullable = false, unique = true)
    private String tokenId; // Format: HT-TOKEN-YYYYMM-XXXX
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tokenAmount; // Token amount
    
    @Column(nullable = false)
    private String tokenType; // REHABILITATION, MEDICATION_COMPLIANCE, HEALTHY_LIFESTYLE, CHECKUP
    
    @Column(nullable = false)
    private String status; // ACTIVE, USED, EXPIRED
    
    // Blockchain Integration
    @Column(length = 500)
    private String blockchainHash; // SHA-256 hash stored on blockchain
    
    @Column(length = 1000)
    private String blockchainReference; // IPFS CID or blockchain transaction ID
    
    @Column(nullable = false)
    private Boolean isBlockchainVerified;
    
    // Activity Details
    @Column(length = 2000)
    private String activityDescription; // What activity earned the token
    
    @Column(length = 1000)
    private String proofData; // JSON string with proof (IoT data, exercise logs, etc.)
    
    // Redemption
    @Column
    private LocalDateTime redeemedAt;
    
    @Column(length = 500)
    private String redemptionType; // DISCOUNT, FREE_ACCOMMODATION, FREE_CHECKUP
    
    @Column
    private Long redemptionReservationId; // Reservation where token was used
    
    // Token Economics (Burn Mechanism)
    @Column(precision = 10, scale = 2)
    private BigDecimal burnAmount; // Amount burned on redemption (5% of tokenAmount)
    
    @Column(precision = 10, scale = 2)
    private BigDecimal redeemedAmount; // Actual amount redeemed after burn
    
    // Timestamps
    @Column(nullable = false)
    private LocalDateTime earnedAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt; // Token expiration (typically 1 year)
    
    @PrePersist
    protected void onCreate() {
        earnedAt = LocalDateTime.now();
        if (expiresAt == null) {
            expiresAt = earnedAt.plusYears(1); // Valid for 1 year
        }
        if (tokenId == null || tokenId.isEmpty()) {
            tokenId = generateTokenId();
        }
        if (status == null || status.isEmpty()) {
            status = "ACTIVE";
        }
        if (isBlockchainVerified == null) {
            isBlockchainVerified = false;
        }
    }
    
    private String generateTokenId() {
        String yearMonth = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
        long randomNum = (long) (Math.random() * 10000);
        return String.format("HT-TOKEN-%s-%04d", yearMonth, randomNum);
    }
}
