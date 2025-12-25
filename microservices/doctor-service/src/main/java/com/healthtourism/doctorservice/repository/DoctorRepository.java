package com.healthtourism.doctorservice.repository;

import com.healthtourism.doctorservice.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Doctor Repository
 * 
 * Professional repository with:
 * - Pagination support
 * - Soft delete filtering (deleted = false) via @Where annotation on Entity
 * - Custom queries for business needs
 * 
 * Note: @Where annotation on Doctor entity automatically filters deleted records,
 * so all queries implicitly exclude deleted doctors.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    /**
     * Find doctors by hospital ID (with pagination)
     */
    Page<Doctor> findByHospitalIdAndIsAvailableTrue(Long hospitalId, Pageable pageable);
    
    /**
     * Find doctors by hospital ID (without pagination - for backward compatibility)
     */
    List<Doctor> findByHospitalIdAndIsAvailableTrue(Long hospitalId);
    
    /**
     * Find doctors by specialization (with pagination)
     */
    Page<Doctor> findBySpecializationAndIsAvailableTrue(String specialization, Pageable pageable);
    
    /**
     * Find doctors by specialization (without pagination - for backward compatibility)
     */
    List<Doctor> findBySpecializationAndIsAvailableTrue(String specialization);
    
    /**
     * Find top rated doctors by hospital (with pagination)
     */
    Page<Doctor> findByHospitalIdAndIsAvailableTrueOrderByRatingDesc(Long hospitalId, Pageable pageable);
    
    /**
     * Find top rated doctors by hospital (without pagination - for backward compatibility)
     */
    @Query("SELECT d FROM Doctor d WHERE d.hospitalId = :hospitalId AND d.isAvailable = true ORDER BY d.rating DESC")
    List<Doctor> findByHospitalIdOrderByRatingDesc(@Param("hospitalId") Long hospitalId);
    
    /**
     * Find doctors by specialization ordered by rating (without pagination - for backward compatibility)
     */
    @Query("SELECT d FROM Doctor d WHERE d.isAvailable = true AND d.specialization = :specialization ORDER BY d.rating DESC")
    List<Doctor> findBySpecializationOrderByRatingDesc(@Param("specialization") String specialization);
    
    /**
     * Find doctor by ID (only if available)
     */
    Optional<Doctor> findByIdAndIsAvailableTrue(Long id);
    
    /**
     * Check if doctor exists and is available
     */
    boolean existsByIdAndIsAvailableTrue(Long id);
    
    /**
     * Count doctors by hospital
     */
    long countByHospitalIdAndIsAvailableTrue(Long hospitalId);
}

