package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    List<Hospital> findByCityAndIsActiveTrue(String city);
    
    List<Hospital> findByDistrictAndIsActiveTrue(String district);
    
    @Query("SELECT h FROM Hospital h WHERE h.isActive = true ORDER BY h.rating DESC")
    List<Hospital> findAllActiveOrderByRatingDesc();
    
    @Query("SELECT h FROM Hospital h WHERE h.isActive = true AND h.airportDistance <= :maxDistance ORDER BY h.airportDistance ASC")
    List<Hospital> findByAirportDistanceLessThanEqual(@Param("maxDistance") Double maxDistance);
    
    Optional<Hospital> findByIdAndIsActiveTrue(Long id);
}

