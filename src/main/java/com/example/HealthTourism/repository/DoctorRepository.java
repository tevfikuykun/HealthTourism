package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Enterprise-grade DoctorRepository with flexible search, N+1 query prevention,
 * multi-language support, and dynamic filtering capabilities.
 * 
 * Features:
 * - JOIN FETCH for N+1 query prevention
 * - Flexible specialization search (LIKE/Containing)
 * - Multi-language support (critical for health tourism)
 * - Premium doctor queries (experience and rating based)
 * - Pagination support for large datasets
 * - JpaSpecificationExecutor for dynamic queries
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {
    
    // ==================== Performance-optimized queries with JOIN FETCH ====================
    
    /**
     * Finds doctor by ID with hospital data eagerly fetched (prevents N+1).
     * Use this when you need hospital information to avoid additional queries.
     */
    @Query("SELECT d FROM Doctor d JOIN FETCH d.hospital WHERE d.id = :id AND d.isAvailable = true")
    Optional<Doctor> findByIdWithHospitalDetails(@Param("id") Long id);
    
    /**
     * Finds doctors by hospital with hospital data eagerly fetched and pagination.
     */
    @Query(value = "SELECT d FROM Doctor d JOIN FETCH d.hospital WHERE d.hospital.id = :hospitalId AND d.isAvailable = true",
           countQuery = "SELECT COUNT(d) FROM Doctor d WHERE d.hospital.id = :hospitalId AND d.isAvailable = true")
    Page<Doctor> findByHospitalIdWithHospital(@Param("hospitalId") Long hospitalId, Pageable pageable);
    
    // ==================== Flexible Specialization Search ====================
    
    /**
     * Flexible specialization search using LIKE (case-insensitive).
     * Example: Searching for "Cerrahi" will find "Genel Cerrahi", "Plastik Cerrahi", etc.
     * Critical for health tourism: patients may search with partial terms.
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.specialization) LIKE LOWER(CONCAT('%', :spec, '%')) " +
           "AND d.isAvailable = true " +
           "ORDER BY d.rating DESC, d.totalReviews DESC")
    List<Doctor> searchBySpecialization(@Param("spec") String specialization);
    
    /**
     * Flexible specialization search with pagination.
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.specialization) LIKE LOWER(CONCAT('%', :spec, '%')) " +
           "AND d.isAvailable = true " +
           "ORDER BY d.rating DESC, d.totalReviews DESC")
    Page<Doctor> searchBySpecialization(@Param("spec") String specialization, Pageable pageable);
    
    /**
     * Exact specialization match with pagination (for backward compatibility).
     */
    Page<Doctor> findBySpecializationAndIsAvailableTrue(String specialization, Pageable pageable);
    
    // ==================== Multi-Language Support (Critical for Health Tourism) ====================
    
    /**
     * Finds doctors who speak a specific language.
     * Languages are stored as comma-separated string: "English, Turkish, Arabic"
     * This query searches for partial matches (case-insensitive).
     * 
     * Critical for health tourism: patients must find doctors who speak their language.
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.languages) LIKE LOWER(CONCAT('%', :language, '%')) " +
           "AND d.isAvailable = true " +
           "ORDER BY d.rating DESC")
    List<Doctor> findByLanguage(@Param("language") String language);
    
    /**
     * Finds doctors who speak a specific language with pagination.
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.languages) LIKE LOWER(CONCAT('%', :language, '%')) " +
           "AND d.isAvailable = true " +
           "ORDER BY d.rating DESC")
    Page<Doctor> findByLanguage(@Param("language") String language, Pageable pageable);
    
    /**
     * Finds doctors by language and specialization (multi-criteria search).
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.languages) LIKE LOWER(CONCAT('%', :language, '%')) " +
           "AND LOWER(d.specialization) LIKE LOWER(CONCAT('%', :spec, '%')) " +
           "AND d.isAvailable = true " +
           "ORDER BY d.rating DESC")
    Page<Doctor> findByLanguageAndSpecialization(
            @Param("language") String language,
            @Param("spec") String specialization,
            Pageable pageable);
    
    // ==================== Premium Doctor Queries ====================
    
    /**
     * Finds top doctors based on minimum experience and rating.
     * Used for premium doctor listings.
     */
    @Query("SELECT d FROM Doctor d WHERE d.experienceYears >= :minExp " +
           "AND d.rating >= :minRating " +
           "AND d.isAvailable = true " +
           "ORDER BY d.rating DESC, d.experienceYears DESC, d.totalReviews DESC")
    List<Doctor> findTopDoctors(
            @Param("minExp") Integer minExp,
            @Param("minRating") Double minRating);
    
    /**
     * Finds top doctors with pagination.
     */
    @Query("SELECT d FROM Doctor d WHERE d.experienceYears >= :minExp " +
           "AND d.rating >= :minRating " +
           "AND d.isAvailable = true " +
           "ORDER BY d.rating DESC, d.experienceYears DESC, d.totalReviews DESC")
    Page<Doctor> findTopDoctors(
            @Param("minExp") Integer minExp,
            @Param("minRating") Double minRating,
            Pageable pageable);
    
    // ==================== Pagination Support ====================
    
    /**
     * Finds all available doctors with pagination.
     * Enterprise requirement: Always use pagination for list queries.
     */
    Page<Doctor> findByIsAvailableTrue(Pageable pageable);
    
    /**
     * Finds doctors by hospital with pagination.
     */
    Page<Doctor> findByHospitalIdAndIsAvailableTrue(Long hospitalId, Pageable pageable);
    
    /**
     * Finds top-rated doctors by hospital with pagination.
     */
    @Query("SELECT d FROM Doctor d WHERE d.hospital.id = :hospitalId AND d.isAvailable = true " +
           "ORDER BY d.rating DESC, d.totalReviews DESC")
    Page<Doctor> findByHospitalIdOrderByRatingDesc(@Param("hospitalId") Long hospitalId, Pageable pageable);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    
    /**
     * @deprecated Use findByHospitalIdAndIsAvailableTrue(Long hospitalId, Pageable pageable) instead.
     * This method loads all records into memory which is not scalable.
     */
    @Deprecated
    List<Doctor> findByHospitalIdAndIsAvailableTrue(Long hospitalId);
    
    /**
     * @deprecated Use searchBySpecialization(String specialization) or 
     * findBySpecializationAndIsAvailableTrue(String specialization, Pageable pageable) instead.
     */
    @Deprecated
    List<Doctor> findBySpecializationAndIsAvailableTrue(String specialization);
    
    /**
     * @deprecated Use findByHospitalIdOrderByRatingDesc(Long hospitalId, Pageable pageable) instead.
     */
    @Deprecated
    @Query("SELECT d FROM Doctor d WHERE d.hospital.id = :hospitalId AND d.isAvailable = true ORDER BY d.rating DESC")
    List<Doctor> findByHospitalIdOrderByRatingDesc(@Param("hospitalId") Long hospitalId);
    
    /**
     * @deprecated Use searchBySpecialization(String specialization) instead for flexible search.
     */
    @Deprecated
    @Query("SELECT d FROM Doctor d WHERE d.isAvailable = true AND d.specialization = :specialization ORDER BY d.rating DESC")
    List<Doctor> findBySpecializationOrderByRatingDesc(@Param("specialization") String specialization);
    
    /**
     * Basic find by ID and available status.
     * For better performance when hospital data is needed, use findByIdWithHospitalDetails instead.
     */
    Optional<Doctor> findByIdAndIsAvailableTrue(Long id);
}

