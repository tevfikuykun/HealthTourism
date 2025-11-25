package com.healthtourism.visaconsultationservice.repository;

import com.healthtourism.visaconsultationservice.entity.VisaConsultancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisaConsultancyRepository extends JpaRepository<VisaConsultancy, Long> {
    List<VisaConsultancy> findByIsAvailableTrue();
    List<VisaConsultancy> findByVisaTypeAndIsAvailableTrue(String visaType);
    List<VisaConsultancy> findByCountryAndIsAvailableTrue(String country);
    List<VisaConsultancy> findByCountryAndVisaTypeAndIsAvailableTrue(String country, String visaType);
    @Query("SELECT v FROM VisaConsultancy v WHERE v.isAvailable = true ORDER BY v.rating DESC")
    List<VisaConsultancy> findAllActiveOrderByRatingDesc();
    Optional<VisaConsultancy> findByIdAndIsAvailableTrue(Long id);
}

