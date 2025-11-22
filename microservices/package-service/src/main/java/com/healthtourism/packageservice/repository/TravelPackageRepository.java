package com.healthtourism.packageservice.repository;
import com.healthtourism.packageservice.entity.TravelPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {
    List<TravelPackage> findByIsActiveTrue();
    List<TravelPackage> findByHospitalIdAndIsActiveTrue(Long hospitalId);
    List<TravelPackage> findByPackageTypeAndIsActiveTrue(String packageType);
    @Query("SELECT t FROM TravelPackage t WHERE t.isActive = true AND t.finalPrice <= :maxPrice ORDER BY t.finalPrice ASC")
    List<TravelPackage> findByFinalPriceLessThanEqual(@Param("maxPrice") BigDecimal maxPrice);
    @Query("SELECT t FROM TravelPackage t WHERE t.isActive = true ORDER BY t.rating DESC")
    List<TravelPackage> findAllActiveOrderByRatingDesc();
    Optional<TravelPackage> findByIdAndIsActiveTrue(Long id);
}

