package com.healthtourism.gamificationservice.repository;

import com.healthtourism.gamificationservice.entity.HealthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthTokenRepository extends JpaRepository<HealthToken, Long> {
    List<HealthToken> findByUserIdOrderByEarnedAtDesc(Long userId);
    List<HealthToken> findByUserIdAndStatus(Long userId, String status);
    Optional<HealthToken> findByTokenId(String tokenId);
    List<HealthToken> findByReservationId(Long reservationId);
    List<HealthToken> findByUserIdAndEarnedAtAfter(Long userId, LocalDateTime date);
}
