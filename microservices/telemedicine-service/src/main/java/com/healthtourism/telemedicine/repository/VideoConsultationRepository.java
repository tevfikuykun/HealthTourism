package com.healthtourism.telemedicine.repository;

import com.healthtourism.telemedicine.entity.VideoConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoConsultationRepository extends JpaRepository<VideoConsultation, Long> {
    List<VideoConsultation> findByPatientIdOrderByScheduledAtDesc(Long patientId);
    List<VideoConsultation> findByDoctorIdOrderByScheduledAtDesc(Long doctorId);
    List<VideoConsultation> findByStatus(VideoConsultation.ConsultationStatus status);
    List<VideoConsultation> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);
    Optional<VideoConsultation> findByConsultationRoomId(String roomId);
}
