package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.VisaConsultancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisaConsultancyRepository extends JpaRepository<VisaConsultancy, Long> {
    List<VisaConsultancy> findByIsAvailableTrue();
    
    List<VisaConsultancy> findByVisaTypeAndIsAvailableTrue(String visaType);
    
    List<VisaConsultancy> findByCountryAndIsAvailableTrue(String country);
    
    Optional<VisaConsultancy> findByIdAndIsAvailableTrue(Long id);
}

