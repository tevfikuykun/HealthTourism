package com.healthtourism.gamificationservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBadge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long badgeId;
    
    @Column(nullable = false)
    private LocalDateTime earnedAt;
    
    @PrePersist
    protected void onCreate() {
        earnedAt = LocalDateTime.now();
    }
}

