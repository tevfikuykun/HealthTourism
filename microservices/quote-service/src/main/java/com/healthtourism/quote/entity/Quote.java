package com.healthtourism.quote.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quote {
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
    private Long treatmentId;
    
    @Column(nullable = false)
    private String quoteNumber;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuoteStatus status;
    
    @Column(nullable = false)
    private BigDecimal totalPrice;
    
    @Column(nullable = false)
    private String currency;
    
    @Column(length = 2000)
    private String description;
    
    @Column(length = 2000)
    private String notes;
    
    @Column(nullable = false)
    private LocalDateTime validUntil;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime approvedAt;
    
    private LocalDateTime rejectedAt;
    
    @Column(length = 500)
    private String rejectionReason;
    
    public enum QuoteStatus {
        DRAFT,           // Taslak
        PENDING,         // Beklemede (Doktor incelemesinde)
        SENT,            // Gönderildi (Hastaya gönderildi)
        ACCEPTED,        // Kabul edildi
        REJECTED,        // Reddedildi
        EXPIRED,         // Süresi doldu
        CONVERTED        // Rezervasyona dönüştürüldü
    }
}
