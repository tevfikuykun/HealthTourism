package com.healthtourism.promotionservice.repository;

import com.healthtourism.promotionservice.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCode(String code);
    
    List<Promotion> findByIsActiveTrue();
    
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND " +
           "p.startDate <= :now AND p.endDate >= :now AND " +
           "p.usedCount < p.maxUses")
    List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);
    
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND " +
           "p.code = :code AND " +
           "p.startDate <= :now AND p.endDate >= :now AND " +
           "p.usedCount < p.maxUses")
    Optional<Promotion> findValidPromotionByCode(@Param("code") String code, @Param("now") LocalDateTime now);
    
    List<Promotion> findByApplicableServiceType(String serviceType);
}

