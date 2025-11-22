package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.TranslationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranslationServiceRepository extends JpaRepository<TranslationService, Long> {
    List<TranslationService> findByIsAvailableTrue();
    
    List<TranslationService> findByServiceTypeAndIsAvailableTrue(String serviceType);
    
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true AND t.isAvailableForHospital = true")
    List<TranslationService> findAvailableForHospital();
    
    Optional<TranslationService> findByIdAndIsAvailableTrue(Long id);
}

