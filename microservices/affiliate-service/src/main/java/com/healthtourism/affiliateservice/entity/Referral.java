package com.healthtourism.affiliateservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "referrals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Referral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long affiliateId;
    
    @Column(nullable = false)
    private Long referredUserId;
    
    @Column
    private Long reservationId; // Conversion olduğunda
    
    @Column
    private Double amount; // Rezervasyon tutarı
    
    @Column
    private Double commission; // Kazanılan komisyon
    
    @Column(nullable = false)
    private String status; // CLICKED, CONVERTED, PAID
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "CLICKED";
    }
}

