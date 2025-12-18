package com.healthtourism.telemedicine.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_consultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoConsultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long patientId;
    
    @Column(nullable = false)
    private Long doctorId;
    
    @Column(nullable = false)
    private String consultationRoomId; // WebRTC room ID
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ConsultationStatus status;
    
    @Column(nullable = false)
    private LocalDateTime scheduledAt;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime endedAt;
    
    @Column(length = 2000)
    private String notes;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private String recordingUrl; // Video kayıt URL'i
    
    @Column(nullable = false)
    private Integer durationMinutes;
    
    public enum ConsultationStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }
}
