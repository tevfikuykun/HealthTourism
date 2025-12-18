package com.healthtourism.aihealthcompanionservice.repository;

import com.healthtourism.aihealthcompanionservice.entity.HealthCompanionConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthCompanionConversationRepository extends JpaRepository<HealthCompanionConversation, Long> {
    List<HealthCompanionConversation> findByUserIdOrderByAskedAtDesc(Long userId);
    List<HealthCompanionConversation> findByReservationIdOrderByAskedAtDesc(Long reservationId);
    List<HealthCompanionConversation> findByUrgencyLevel(String urgencyLevel);
    List<HealthCompanionConversation> findByRequiresDoctorReviewTrue();
}
