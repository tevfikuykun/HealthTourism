package com.healthtourism.affiliateservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "affiliates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Affiliate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false, unique = true)
    private String referralCode;
    
    @Column(nullable = false)
    private String referralLink;
    
    @Column
    private Integer totalClicks;
    
    @Column
    private Integer totalConversions;
    
    @Column
    private Double totalEarnings;
    
    @Column
    private Double commissionRate; // Yüzde
    
    @Column(nullable = false)
    private String status; // PENDING, APPROVED, ACTIVE, SUSPENDED
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
        if (totalClicks == null) totalClicks = 0;
        if (totalConversions == null) totalConversions = 0;
        if (totalEarnings == null) totalEarnings = 0.0;
        if (commissionRate == null) commissionRate = 10.0; // %10 default
    }
}

