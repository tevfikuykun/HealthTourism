package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    List<Accommodation> findByHospitalIdAndIsActiveTrue(Long hospitalId);
    
    @Query("SELECT a FROM Accommodation a WHERE a.hospital.id = :hospitalId AND a.isActive = true ORDER BY a.distanceToHospital ASC")
    List<Accommodation> findByHospitalIdOrderByDistanceAsc(@Param("hospitalId") Long hospitalId);
    
    @Query("SELECT a FROM Accommodation a WHERE a.isActive = true AND a.pricePerNight <= :maxPrice ORDER BY a.pricePerNight ASC")
    List<Accommodation> findByPricePerNightLessThanEqual(@Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT a FROM Accommodation a WHERE a.hospital.id = :hospitalId AND a.isActive = true ORDER BY a.rating DESC")
    List<Accommodation> findByHospitalIdOrderByRatingDesc(@Param("hospitalId") Long hospitalId);
    
    Optional<Accommodation> findByIdAndIsActiveTrue(Long id);
}

