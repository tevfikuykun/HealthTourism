package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Enterprise-grade AccommodationRepository with pagination support,
 * N+1 query prevention, and dynamic filtering capabilities.
 * 
 * Performance optimizations:
 * - JOIN FETCH prevents N+1 query problems
 * - Pagination support for large datasets
 * - JpaSpecificationExecutor for dynamic queries
 */
@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long>, JpaSpecificationExecutor<Accommodation> {
    
    // ==================== Performance-optimized queries with JOIN FETCH ====================
    
    /**
     * Finds accommodation by ID with hospital data eagerly fetched (prevents N+1).
     * Use this when you need hospital information to avoid additional queries.
     */
    @Query("SELECT a FROM Accommodation a JOIN FETCH a.hospital WHERE a.id = :id AND a.isActive = true")
    Optional<Accommodation> findByIdWithHospital(@Param("id") Long id);
    
    // ==================== Pagination support ====================
    
    /**
     * Finds accommodations by hospital ID with pagination support.
     * Enterprise requirement: Always use pagination for list queries.
     */
    @Query("SELECT a FROM Accommodation a WHERE a.hospital.id = :hospitalId AND a.isActive = true")
    Page<Accommodation> findByHospitalId(@Param("hospitalId") Long hospitalId, Pageable pageable);
    
    /**
     * Finds accommodations by hospital ID with hospital data eagerly fetched and pagination.
     */
    @Query(value = "SELECT a FROM Accommodation a JOIN FETCH a.hospital WHERE a.hospital.id = :hospitalId AND a.isActive = true",
           countQuery = "SELECT COUNT(a) FROM Accommodation a WHERE a.hospital.id = :hospitalId AND a.isActive = true")
    Page<Accommodation> findByHospitalIdWithHospital(@Param("hospitalId") Long hospitalId, Pageable pageable);
    
    /**
     * Multi-criteria search with pagination support.
     * Filters by city and maximum price per night.
     */
    @Query("SELECT a FROM Accommodation a WHERE a.city = :city " +
           "AND a.pricePerNight <= :maxPrice " +
           "AND a.isActive = true")
    Page<Accommodation> searchAccommodations(
            @Param("city") String city, 
            @Param("maxPrice") BigDecimal maxPrice, 
            Pageable pageable);
    
    /**
     * Finds accommodations near a hospital within specified distance (km).
     * Optimized for health tourism use case - finding hotels close to hospitals.
     * Note: Returns List for nearby results (typically small dataset).
     */
    @Query("SELECT a FROM Accommodation a JOIN FETCH a.hospital " +
           "WHERE a.hospital.id = :hospitalId " +
           "AND a.distanceToHospital <= :maxKm " +
           "AND a.isActive = true " +
           "ORDER BY a.distanceToHospital ASC")
    List<Accommodation> findNearbyAccommodations(
            @Param("hospitalId") Long hospitalId, 
            @Param("maxKm") Double maxKm);
    
    /**
     * Finds accommodations by price range with pagination.
     */
    @Query("SELECT a FROM Accommodation a WHERE a.isActive = true " +
           "AND a.pricePerNight >= :minPrice AND a.pricePerNight <= :maxPrice " +
           "ORDER BY a.pricePerNight ASC")
    Page<Accommodation> findByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
    
    /**
     * Finds top-rated accommodations by hospital with pagination.
     */
    @Query("SELECT a FROM Accommodation a WHERE a.hospital.id = :hospitalId " +
           "AND a.isActive = true " +
           "ORDER BY a.rating DESC, a.totalReviews DESC")
    Page<Accommodation> findTopRatedByHospital(
            @Param("hospitalId") Long hospitalId,
            Pageable pageable);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    // Consider migrating service layer to use paginated versions above
    
    /**
     * @deprecated Use findByHospitalId(Long hospitalId, Pageable pageable) instead.
     * This method loads all records into memory which is not scalable.
     */
    @Deprecated
    List<Accommodation> findByHospitalIdAndIsActiveTrue(Long hospitalId);
    
    /**
     * @deprecated Use findNearbyAccommodations with pagination or findTopRatedByHospital instead.
     */
    @Deprecated
    @Query("SELECT a FROM Accommodation a WHERE a.hospital.id = :hospitalId AND a.isActive = true ORDER BY a.distanceToHospital ASC")
    List<Accommodation> findByHospitalIdOrderByDistanceAsc(@Param("hospitalId") Long hospitalId);
    
    /**
     * @deprecated Use findByPriceRange instead.
     */
    @Deprecated
    @Query("SELECT a FROM Accommodation a WHERE a.isActive = true AND a.pricePerNight <= :maxPrice ORDER BY a.pricePerNight ASC")
    List<Accommodation> findByPricePerNightLessThanEqual(@Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * @deprecated Use findTopRatedByHospital instead.
     */
    @Deprecated
    @Query("SELECT a FROM Accommodation a WHERE a.hospital.id = :hospitalId AND a.isActive = true ORDER BY a.rating DESC")
    List<Accommodation> findByHospitalIdOrderByRatingDesc(@Param("hospitalId") Long hospitalId);
    
    /**
     * Basic find by ID and active status.
     * For better performance when hospital data is needed, use findByIdWithHospital instead.
     */
    Optional<Accommodation> findByIdAndIsActiveTrue(Long id);
}

