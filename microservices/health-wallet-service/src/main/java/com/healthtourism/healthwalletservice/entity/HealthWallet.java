package com.healthtourism.healthwalletservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Unified Health Wallet
 * Contains all patient health data: IPFS documents, blockchain insurance, IoT history
 */
@Entity
@Table(name = "health_wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long userId;
    
    @Column(nullable = false, unique = true)
    private String walletId; // Unique wallet identifier (UUID)
    
    @Column(nullable = false, unique = true)
    private String qrCodeHash; // Hash for QR code generation
    
    // IPFS Document References
    @Column(length = 5000)
    private String ipfsDocumentReferences; // JSON array of IPFS CIDs
    
    @Column
    private Integer documentCount;
    
    // Blockchain Insurance Policy
    @Column
    private Long insurancePolicyId;
    
    @Column(length = 100)
    private String insurancePolicyHash; // Blockchain hash
    
    @Column
    private Boolean hasInsurance;
    
    // IoT Monitoring History
    @Column
    private Long latestIotDataId;
    
    @Column(length = 100)
    private String iotDataSummary; // JSON summary of IoT data
    
    @Column
    private Integer iotDataPointCount;
    
    // Recovery Score
    @Column
    private Long latestRecoveryScoreId;
    
    @Column(length = 50)
    private String currentRecoveryScore; // Current recovery score
    
    // Legal Documents (Legal Ledger)
    @Column(length = 5000)
    private String legalDocumentReferences; // JSON array of legal document IDs
    
    @Column
    private Integer legalDocumentCount;
    
    // Wallet Status
    @Column(nullable = false)
    private String status; // ACTIVE, SUSPENDED, ARCHIVED
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;
    
    @Column
    private LocalDateTime lastAccessedAt;
    
    @Column
    private Integer accessCount; // Number of times wallet was accessed
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdatedAt = LocalDateTime.now();
        if (status == null) {
            status = "ACTIVE";
        }
        if (accessCount == null) {
            accessCount = 0;
        }
        if (hasInsurance == null) {
            hasInsurance = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
    }
}
