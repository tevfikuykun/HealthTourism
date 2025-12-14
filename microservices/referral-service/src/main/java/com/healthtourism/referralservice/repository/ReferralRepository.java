package com.healthtourism.referralservice.repository;

import com.healthtourism.referralservice.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Optional<Referral> findByUserId(Long userId);
    Optional<Referral> findByCode(String code);
}

