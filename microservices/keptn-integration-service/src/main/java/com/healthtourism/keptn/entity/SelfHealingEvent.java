package com.healthtourism.keptn.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "self_healing_events")
@Data
public class SelfHealingEvent {
    @Id
    private String id;
    
    @Column(nullable = false)
    private String type; // AUTO_FIXED, AUTO_SCALED, AUTO_ROLLBACK, AUTO_RESTART
    
    @Column(nullable = false)
    private String serviceName;
    
    @Column(nullable = false)
    private String action;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String status; // SUCCESS, FAILED
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    private Long responseTimeMs;
    
    private String result;
}



