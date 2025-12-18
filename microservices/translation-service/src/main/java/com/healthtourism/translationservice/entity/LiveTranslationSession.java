package com.healthtourism.translationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Live Translation Session
 * Real-time translation during telemedicine consultations
 */
@Entity
@Table(name = "live_translation_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveTranslationSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long consultationId; // Telemedicine consultation ID
    
    @Column(nullable = false)
    private Long userId; // Patient user ID
    
    @Column(nullable = false)
    private Long doctorId; // Doctor ID
    
    @Column(nullable = false)
    private String sourceLanguage; // Patient's language (e.g., "en", "ar", "de")
    
    @Column(nullable = false)
    private String targetLanguage; // Doctor's language (e.g., "tr")
    
    @Column(nullable = false)
    private String status; // ACTIVE, COMPLETED, CANCELLED
    
    // Translation Settings
    @Column(nullable = false)
    private Boolean enableSubtitles; // Show subtitles
    
    @Column(nullable = false)
    private Boolean enableVoiceTranslation; // Real-time voice translation
    
    @Column(nullable = false)
    private String translationProvider; // GOOGLE, AZURE, CUSTOM
    
    // Session Data
    @Column(length = 10000)
    private String translationHistory; // JSON string with translation history
    
    @Column(nullable = false)
    private Integer totalTranslations; // Number of translations performed
    
    @Column(nullable = false)
    private Double averageAccuracy; // Average translation accuracy
    
    // Timestamps
    @Column(nullable = false)
    private LocalDateTime startedAt;
    
    @Column
    private LocalDateTime endedAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
        if (status == null || status.isEmpty()) {
            status = "ACTIVE";
        }
        if (totalTranslations == null) {
            totalTranslations = 0;
        }
        if (averageAccuracy == null) {
            averageAccuracy = 0.0;
        }
    }
}
