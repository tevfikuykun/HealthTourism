package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.TransferService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransferServiceRepository extends JpaRepository<TransferService, Long> {
    List<TransferService> findByIsAvailableTrue();
    
    List<TransferService> findByServiceTypeAndIsAvailableTrue(String serviceType);
    
    @Query("SELECT t FROM TransferService t WHERE t.isAvailable = true AND t.price <= :maxPrice ORDER BY t.price ASC")
    List<TransferService> findByPriceLessThanEqual(@Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT t FROM TransferService t WHERE t.isAvailable = true ORDER BY t.rating DESC")
    List<TransferService> findAllAvailableOrderByRatingDesc();
    
    Optional<TransferService> findByIdAndIsAvailableTrue(Long id);
}

