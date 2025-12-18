package com.healthtourism.virtualtourservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * AR/VR Virtual Tour Entity
 * Supports 360-degree tours for hospitals, accommodations, and doctor offices
 */
@Entity
@Table(name = "virtual_tours")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirtualTour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String tourType; // HOSPITAL, ACCOMMODATION, DOCTOR_OFFICE, OPERATING_ROOM
    
    @Column(nullable = false)
    private Long entityId; // Hospital ID, Accommodation ID, or Doctor ID
    
    @Column(nullable = false)
    private String entityName; // Name of the hospital/accommodation/doctor
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    // 360-Degree Tour Assets
    @Column(nullable = false, length = 1000)
    private String tourUrl; // Main 360-degree tour URL (WebRTC/WebGL)
    
    @Column(length = 1000)
    private String thumbnailUrl; // Thumbnail image
    
    @Column(length = 1000)
    private String panoramaImageUrl; // 360-degree panorama image
    
    @Column(length = 1000)
    private String vrVideoUrl; // VR video URL (optional)
    
    // AR Support
    @Column(nullable = false)
    private Boolean supportsAR; // Whether AR mode is supported
    
    @Column(length = 1000)
    private String arModelUrl; // AR model URL (GLB/GLTF format)
    
    // Tour Metadata
    @Column(nullable = false)
    private Integer durationSeconds; // Estimated tour duration
    
    @Column(nullable = false)
    private Integer hotspotCount; // Number of interactive hotspots
    
    @Column(nullable = false)
    private String status; // ACTIVE, INACTIVE, DRAFT
    
    // Location Info
    @Column(length = 500)
    private String location; // Room name, floor, etc.
    
    @Column(length = 1000)
    private String tags; // Comma-separated tags
    
    // Statistics
    @Column(nullable = false)
    private Integer viewCount;
    
    @Column(nullable = false)
    private Double averageRating;
    
    @Column(nullable = false)
    private Integer totalRatings;
    
    // Timestamps
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (viewCount == null) viewCount = 0;
        if (averageRating == null) averageRating = 0.0;
        if (totalRatings == null) totalRatings = 0;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
