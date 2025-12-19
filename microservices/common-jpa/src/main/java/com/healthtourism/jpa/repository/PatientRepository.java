package com.healthtourism.jpa.repository;

import com.healthtourism.jpa.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

/**
 * Patient Repository with Hibernate Caching
 * L2 Cache (Redis) enabled for queries
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    /**
     * Find by email with L2 cache
     * Query result cached in Redis
     */
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Optional<Patient> findByEmail(String email);
    
    /**
     * Find by national ID (encrypted field)
     * Cached for performance
     */
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Optional<Patient> findByNationalId(String nationalId);
    
    /**
     * Custom query with cache
     */
    @Query("SELECT p FROM Patient p WHERE p.dateOfBirth >= :startDate AND p.dateOfBirth <= :endDate")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Patient> findPatientsByDateRange(
        @Param("startDate") java.time.LocalDate startDate,
        @Param("endDate") java.time.LocalDate endDate
    );
    
    /**
     * Find with medical history (L2 cache)
     */
    @Query("SELECT p FROM Patient p WHERE p.medicalHistory LIKE %:keyword%")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Patient> findByMedicalHistoryContaining(@Param("keyword") String keyword);
}

