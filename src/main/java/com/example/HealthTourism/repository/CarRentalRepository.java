package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.CarRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRentalRepository extends JpaRepository<CarRental, Long> {
    List<CarRental> findByIsAvailableTrue();
    
    List<CarRental> findByCarTypeAndIsAvailableTrue(String carType);
    
    @Query("SELECT c FROM CarRental c WHERE c.isAvailable = true AND c.pricePerDay <= :maxPrice ORDER BY c.pricePerDay ASC")
    List<CarRental> findByPricePerDayLessThanEqual(@Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT c FROM CarRental c WHERE c.isAvailable = true ORDER BY c.rating DESC")
    List<CarRental> findAllAvailableOrderByRatingDesc();
    
    Optional<CarRental> findByIdAndIsAvailableTrue(Long id);
}

