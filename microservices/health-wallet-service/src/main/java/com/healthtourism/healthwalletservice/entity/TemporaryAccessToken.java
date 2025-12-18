package com.healthtourism.healthwalletservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Temporary Access Token for Privacy Shield
 * Provides time-limited access to health wallet data (e.g., when QR code is scanned)
 */
@Entity
@Table(name = "temporary_access_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryAccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long walletId;
    
    @Column(nullable = false, unique = true)
    private String token; // Unique access token
    
    @Column(nullable = false)
    private Long authorizedUserId; // Doctor or healthcare provider who scanned QR
    
    @Column(nullable = false)
    private Long patientUserId; // Patient who owns the wallet
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt; // Token expires after 1 hour
    
    @Column(nullable = false)
    private String accessPurpose; // TREATMENT, EMERGENCY, CONSULTATION
    
    @Column(nullable = false)
    private String status; // ACTIVE, EXPIRED, REVOKED
    
    @Column
    private LocalDateTime lastAccessedAt;
    
    @Column
    private Integer accessCount; // Number of times token was used
    
    @Column(length = 2000)
    private String ipfsPreSignedUrls; // JSON array of pre-signed URLs for IPFS documents
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        expiresAt = createdAt.plusHours(1); // 1 hour validity
        if (status == null) {
            status = "ACTIVE";
        }
        if (accessCount == null) {
            accessCount = 0;
        }
        if (token == null || token.isEmpty()) {
            token = generateToken();
        }
    }
    
    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "") + 
               System.currentTimeMillis();
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt) || "EXPIRED".equals(status) || "REVOKED".equals(status);
    }
}
