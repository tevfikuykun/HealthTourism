package com.healthtourism.influencerservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "influencers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Influencer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String platform; // INSTAGRAM, YOUTUBE, TIKTOK, FACEBOOK
    
    @Column(nullable = false)
    private String username;
    
    @Column
    private String profileUrl;
    
    @Column
    private Integer followerCount;
    
    @Column
    private Double engagementRate;
    
    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED, ACTIVE
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }
}

