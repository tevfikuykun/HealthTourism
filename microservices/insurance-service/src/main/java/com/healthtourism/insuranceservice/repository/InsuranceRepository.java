package com.healthtourism.insuranceservice.repository;
import com.healthtourism.insuranceservice.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
    List<Insurance> findByIsActiveTrue();
    List<Insurance> findByInsuranceTypeAndIsActiveTrue(String insuranceType);
    List<Insurance> findByCoverageAreaAndIsActiveTrue(String coverageArea);
    @Query("SELECT i FROM Insurance i WHERE i.isActive = true AND i.premium <= :maxPremium ORDER BY i.premium ASC")
    List<Insurance> findByPremiumLessThanEqual(@Param("maxPremium") BigDecimal maxPremium);
    @Query("SELECT i FROM Insurance i WHERE i.isActive = true ORDER BY i.rating DESC")
    List<Insurance> findAllActiveOrderByRatingDesc();
    Optional<Insurance> findByIdAndIsActiveTrue(Long id);
}

