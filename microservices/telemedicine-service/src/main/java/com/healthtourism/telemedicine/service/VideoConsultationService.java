package com.healthtourism.telemedicine.service;

import com.healthtourism.telemedicine.entity.VideoConsultation;
import com.healthtourism.telemedicine.repository.VideoConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Telemedicine Video Consultation Service
 * Manages WebRTC-based video consultations
 */
@Service
public class VideoConsultationService {
    
    @Autowired
    private VideoConsultationRepository consultationRepository;
    
    @Transactional
    public VideoConsultation scheduleConsultation(Long patientId, Long doctorId, LocalDateTime scheduledAt) {
        VideoConsultation consultation = new VideoConsultation();
        consultation.setPatientId(patientId);
        consultation.setDoctorId(doctorId);
        consultation.setConsultationRoomId("room-" + UUID.randomUUID().toString());
        consultation.setStatus(VideoConsultation.ConsultationStatus.SCHEDULED);
        consultation.setScheduledAt(scheduledAt);
        consultation.setCreatedAt(LocalDateTime.now());
        consultation.setDurationMinutes(30); // Default 30 minutes
        
        return consultationRepository.save(consultation);
    }
    
    @Transactional
    public VideoConsultation startConsultation(String roomId) {
        VideoConsultation consultation = consultationRepository.findByConsultationRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Konsültasyon bulunamadı"));
        
        if (consultation.getStatus() != VideoConsultation.ConsultationStatus.SCHEDULED) {
            throw new RuntimeException("Sadece planlanmış konsültasyonlar başlatılabilir");
        }
        
        consultation.setStatus(VideoConsultation.ConsultationStatus.IN_PROGRESS);
        consultation.setStartedAt(LocalDateTime.now());
        
        return consultationRepository.save(consultation);
    }
    
    @Transactional
    public VideoConsultation endConsultation(String roomId, String notes) {
        VideoConsultation consultation = consultationRepository.findByConsultationRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Konsültasyon bulunamadı"));
        
        if (consultation.getStatus() != VideoConsultation.ConsultationStatus.IN_PROGRESS) {
            throw new RuntimeException("Sadece devam eden konsültasyonlar sonlandırılabilir");
        }
        
        consultation.setStatus(VideoConsultation.ConsultationStatus.COMPLETED);
        consultation.setEndedAt(LocalDateTime.now());
        consultation.setNotes(notes);
        
        // Calculate duration
        if (consultation.getStartedAt() != null) {
            long minutes = java.time.Duration.between(consultation.getStartedAt(), LocalDateTime.now()).toMinutes();
            consultation.setDurationMinutes((int) minutes);
        }
        
        return consultationRepository.save(consultation);
    }
    
    @Transactional
    public VideoConsultation cancelConsultation(Long consultationId) {
        VideoConsultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new RuntimeException("Konsültasyon bulunamadı"));
        
        consultation.setStatus(VideoConsultation.ConsultationStatus.CANCELLED);
        
        return consultationRepository.save(consultation);
    }
    
    public List<VideoConsultation> getConsultationsByPatient(Long patientId) {
        return consultationRepository.findByPatientIdOrderByScheduledAtDesc(patientId);
    }
    
    public List<VideoConsultation> getConsultationsByDoctor(Long doctorId) {
        return consultationRepository.findByDoctorIdOrderByScheduledAtDesc(doctorId);
    }
    
    public VideoConsultation getConsultationByRoomId(String roomId) {
        return consultationRepository.findByConsultationRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Konsültasyon bulunamadı"));
    }
    
    public String getWebRTCSignalingEndpoint(String roomId) {
        // In production, this would return actual WebRTC signaling server endpoint
        // For now, return a placeholder
        return "/api/telemedicine/webrtc/signaling/" + roomId;
    }
}
