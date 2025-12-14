package com.healthtourism.couponservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private Double discountAmount;
    @Column
    private Double discountPercentage;
    @Column
    private LocalDateTime validFrom;
    @Column
    private LocalDateTime validTo;
    @Column
    private Boolean isActive;
    @Column
    private Integer maxUses;
    @Column
    private Integer usedCount;
    @PrePersist
    protected void onCreate() {
        if (isActive == null) isActive = true;
        if (usedCount == null) usedCount = 0;
    }
}

