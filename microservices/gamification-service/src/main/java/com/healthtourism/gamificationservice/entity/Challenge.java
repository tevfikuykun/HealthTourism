package com.healthtourism.gamificationservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String type; // RESERVATION, REVIEW, REFERRAL, SOCIAL_SHARE
    
    @Column
    private Integer targetCount; // Hedef sayı
    
    @Column
    private Integer pointsReward;
    
    @Column
    private Long badgeRewardId;
    
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    @Column
    private LocalDateTime endDate;
    
    @Column(nullable = false)
    private String status; // ACTIVE, COMPLETED, EXPIRED
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "ACTIVE";
    }
}

