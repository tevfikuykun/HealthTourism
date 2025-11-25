package com.healthtourism.promotionservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // İndirim kodu

    @Column(nullable = false)
    private String name; // Kampanya adı

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String discountType; // PERCENTAGE, FIXED_AMOUNT

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discountValue; // Yüzde veya sabit tutar

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Integer maxUses; // Maksimum kullanım sayısı

    @Column(nullable = false)
    private Integer usedCount; // Kullanılan sayı

    @Column(nullable = false)
    private BigDecimal minPurchaseAmount; // Minimum alışveriş tutarı

    @Column(nullable = false)
    private BigDecimal maxDiscountAmount; // Maksimum indirim tutarı (yüzde indirimlerde)

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private String applicableServiceType; // ALL, HOSPITAL, DOCTOR, PACKAGE, ACCOMMODATION, FLIGHT

    private Long specificServiceId; // Belirli bir servise özel ise

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (usedCount == null) {
            usedCount = 0;
        }
        if (isActive == null) {
            isActive = true;
        }
    }
}

