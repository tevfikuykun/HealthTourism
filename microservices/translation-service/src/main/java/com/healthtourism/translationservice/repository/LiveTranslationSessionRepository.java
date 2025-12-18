package com.healthtourism.translationservice.repository;

import com.healthtourism.translationservice.entity.LiveTranslationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveTranslationSessionRepository extends JpaRepository<LiveTranslationSession, Long> {
    Optional<LiveTranslationSession> findByConsultationId(Long consultationId);
    List<LiveTranslationSession> findByUserIdOrderByStartedAtDesc(Long userId);
    List<LiveTranslationSession> findByStatus(String status);
}
