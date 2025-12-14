package com.healthtourism.influencerservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Long influencerId;
    
    @Column
    private Double budget;
    
    @Column
    private Double commissionRate; // Yüzde
    
    @Column(nullable = false)
    private String status; // DRAFT, ACTIVE, COMPLETED, CANCELLED
    
    @Column
    private LocalDateTime startDate;
    
    @Column
    private LocalDateTime endDate;
    
    @Column
    private Integer clicks;
    
    @Column
    private Integer conversions;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "DRAFT";
    }
}

