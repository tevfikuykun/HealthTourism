package com.healthtourism.authservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * Password Reset Token Entity
 * 
 * Stores password reset tokens with security audit information.
 * Includes IP address and User-Agent for security tracking.
 */
@Entity
@Table(name = "password_reset_tokens", indexes = {
    @Index(name = "idx_reset_token", columnList = "token", unique = true),
    @Index(name = "idx_reset_user_id", columnList = "user_id"),
    @Index(name = "idx_reset_expiry", columnList = "expiry_date"),
    @Index(name = "idx_reset_user_expiry", columnList = "user_id, expiry_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Reset token (UUID)
     */
    @Column(nullable = false, unique = true, length = 255)
    private String token;
    
    /**
     * User ID (foreign key to users table)
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * Token expiry date
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
    
    /**
     * Whether token has been used
     */
    @Column(name = "is_used", nullable = false)
    @Builder.Default
    private Boolean isUsed = false;
    
    /**
     * Client IP address (for security audit)
     */
    @Column(name = "client_ip", length = 45) // IPv6 compatible
    private String clientIp;
    
    /**
     * User-Agent string (for security audit)
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    /**
     * Created timestamp
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Check if token is expired
     */
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDateTime.now());
    }
    
    /**
     * Check if token is valid (not used and not expired)
     */
    public boolean isValid() {
        return !isUsed && !isExpired();
    }
    
    /**
     * Mark token as used
     */
    public void markAsUsed() {
        this.isUsed = true;
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isUsed == null) {
            isUsed = false;
        }
    }
}
