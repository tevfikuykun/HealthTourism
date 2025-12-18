package com.healthtourism.healthwalletservice.repository;

import com.healthtourism.healthwalletservice.entity.TemporaryAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemporaryAccessTokenRepository extends JpaRepository<TemporaryAccessToken, Long> {
    Optional<TemporaryAccessToken> findByToken(String token);
    List<TemporaryAccessToken> findByWalletIdAndStatus(Long walletId, String status);
    List<TemporaryAccessToken> findByPatientUserIdOrderByCreatedAtDesc(Long patientUserId);
}
