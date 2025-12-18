package com.healthtourism.aihealthcompanionservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * AI Health Companion Conversation
 * 7/24 Digital Nurse conversations using RAG (Retrieval-Augmented Generation)
 */
@Entity
@Table(name = "health_companion_conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthCompanionConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long reservationId; // Associated reservation
    
    @Column(nullable = false)
    private Long procedureId; // Procedure/surgery type
    
    @Column(nullable = false)
    private String procedureType; // SURGERY_TYPE, TREATMENT_TYPE, etc.
    
    // Conversation Data
    @Column(nullable = false, length = 10000)
    private String userQuestion; // User's question
    
    @Column(nullable = false, length = 10000)
    private String aiResponse; // AI's response
    
    // RAG Context
    @Column(length = 5000)
    private String retrievedContext; // Retrieved medical knowledge
    
    @Column(length = 2000)
    private String medicalHistoryContext; // User's medical history context
    
    @Column(nullable = false)
    private Double confidenceScore; // AI confidence (0-1)
    
    // Response Classification
    @Column(nullable = false)
    private String responseType; // GENERAL_INFO, SYMPTOM_CHECK, MEDICATION_ADVICE, URGENT_REFERRAL
    
    @Column(nullable = false)
    private String urgencyLevel; // LOW, MEDIUM, HIGH, CRITICAL
    
    // Follow-up Actions
    @Column
    private Boolean requiresDoctorReview; // If AI recommends doctor consultation
    
    @Column
    private Long referredToDoctorId; // Doctor ID if referral needed
    
    @Column(length = 2000)
    private String followUpRecommendations; // JSON string with recommendations
    
    // Timestamps
    @Column(nullable = false)
    private LocalDateTime askedAt;
    
    @Column(nullable = false)
    private LocalDateTime respondedAt;
    
    @PrePersist
    protected void onCreate() {
        askedAt = LocalDateTime.now();
        respondedAt = LocalDateTime.now();
        if (confidenceScore == null) {
            confidenceScore = 0.0;
        }
        if (urgencyLevel == null || urgencyLevel.isEmpty()) {
            urgencyLevel = "LOW";
        }
        if (responseType == null || responseType.isEmpty()) {
            responseType = "GENERAL_INFO";
        }
        if (requiresDoctorReview == null) {
            requiresDoctorReview = false;
        }
    }
}
