package com.healthtourism.migration.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity Version Tracking
 * Tracks semantic versions of entity schemas
 */
@Entity
@Table(name = "entity_versions")
@Data
public class EntityVersion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String entityName; // e.g., "Patient", "Reservation"
    
    @Column(nullable = false)
    private String currentVersion; // Semantic version: "1.2.3"
    
    @Column(nullable = false)
    private String schemaJson; // JSON Schema definition
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column
    private String migrationScript; // Flyway/Liquibase script path
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


