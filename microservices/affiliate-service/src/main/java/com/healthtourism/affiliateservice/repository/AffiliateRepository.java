package com.healthtourism.affiliateservice.repository;
import com.healthtourism.affiliateservice.entity.Affiliate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AffiliateRepository extends JpaRepository<Affiliate, Long> {
    Optional<Affiliate> findByReferralCode(String referralCode);
    Affiliate findByUserId(Long userId);
}

