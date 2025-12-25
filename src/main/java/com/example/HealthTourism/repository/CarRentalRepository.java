package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.CarRental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Enterprise-grade CarRentalRepository with pagination support,
 * capacity-based filtering, price range search, and availability checking
 * to prevent double booking.
 * 
 * Performance optimizations:
 * - Pagination support for large datasets
 * - Capacity-based filtering for health tourism use case
 * - Price range search for better UX
 * - Date conflict checking to prevent double booking
 * - JpaSpecificationExecutor for dynamic queries
 */
@Repository
public interface CarRentalRepository extends JpaRepository<CarRental, Long>, JpaSpecificationExecutor<CarRental> {
    
    // ==================== Pagination support ====================
    
    /**
     * Finds all available cars with pagination support.
     * Enterprise requirement: Always use pagination for list queries.
     */
    Page<CarRental> findByIsAvailableTrue(Pageable pageable);
    
    /**
     * Finds cars by type with pagination support.
     */
    Page<CarRental> findByCarTypeAndIsAvailableTrue(String carType, Pageable pageable);
    
    /**
     * Finds cars by type and minimum passenger capacity with pagination.
     * Critical for health tourism: patients often travel with family.
     */
    @Query("SELECT c FROM CarRental c WHERE c.carType = :type " +
           "AND c.passengerCapacity >= :minCapacity " +
           "AND c.isAvailable = true")
    Page<CarRental> findByTypeAndCapacity(
            @Param("type") String type, 
            @Param("minCapacity") Integer minCapacity,
            Pageable pageable);
    
    /**
     * Multi-criteria search with price range and driver option.
     * Filters by price range and whether driver is included.
     */
    @Query("SELECT c FROM CarRental c WHERE c.isAvailable = true " +
           "AND c.pricePerDay BETWEEN :minPrice AND :maxPrice " +
           "AND c.includesDriver = :includesDriver")
    Page<CarRental> searchCarsByFilter(
            @Param("minPrice") BigDecimal minPrice, 
            @Param("maxPrice") BigDecimal maxPrice, 
            @Param("includesDriver") Boolean includesDriver,
            Pageable pageable);
    
    /**
     * Finds cars by price range with pagination.
     */
    @Query("SELECT c FROM CarRental c WHERE c.isAvailable = true " +
           "AND c.pricePerDay >= :minPrice AND c.pricePerDay <= :maxPrice " +
           "ORDER BY c.pricePerDay ASC")
    Page<CarRental> findByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
    
    /**
     * Location-based search with pagination.
     * Finds cars available at a specific pickup location.
     */
    Page<CarRental> findByPickupLocationContainingIgnoreCaseAndIsAvailableTrue(String location, Pageable pageable);
    
    /**
     * Finds top-rated available cars with pagination.
     */
    @Query("SELECT c FROM CarRental c WHERE c.isAvailable = true " +
           "ORDER BY c.rating DESC, c.totalReviews DESC")
    Page<CarRental> findAllAvailableOrderByRatingDesc(Pageable pageable);
    
    // ==================== Availability checking (prevents double booking) ====================
    
    /**
     * Finds cars that are available for the specified date range.
     * This query excludes cars that have conflicting reservations (double booking prevention).
     * 
     * A car is considered available if:
     * 1. isAvailable = true (static availability)
     * 2. No active reservations exist for the requested date range
     * 
     * Date conflict logic: Two date ranges overlap if:
     * - (pickupDate <= requestedDropoffDate) AND (dropoffDate >= requestedPickupDate)
     * 
     * @param pickupDate Requested pickup date
     * @param dropoffDate Requested dropoff date
     * @return List of available cars for the specified dates
     */
    @Query("SELECT DISTINCT c FROM CarRental c " +
           "WHERE c.isAvailable = true " +
           "AND c.id NOT IN (" +
           "    SELECT cr.carRental.id FROM CarRentalReservation cr " +
           "    WHERE cr.status IN ('CONFIRMED', 'PENDING') " +
           "    AND cr.pickupDate <= :dropoffDate " +
           "    AND cr.dropoffDate >= :pickupDate" +
           ")")
    List<CarRental> findAvailableCarsForDateRange(
            @Param("pickupDate") LocalDate pickupDate,
            @Param("dropoffDate") LocalDate dropoffDate);
    
    /**
     * Finds available cars for date range with additional filters (pagination).
     */
    @Query("SELECT DISTINCT c FROM CarRental c " +
           "WHERE c.isAvailable = true " +
           "AND c.passengerCapacity >= :minCapacity " +
           "AND c.pricePerDay BETWEEN :minPrice AND :maxPrice " +
           "AND c.id NOT IN (" +
           "    SELECT cr.carRental.id FROM CarRentalReservation cr " +
           "    WHERE cr.status IN ('CONFIRMED', 'PENDING') " +
           "    AND cr.pickupDate <= :dropoffDate " +
           "    AND cr.dropoffDate >= :pickupDate" +
           ")")
    Page<CarRental> findAvailableCarsForDateRangeWithFilters(
            @Param("pickupDate") LocalDate pickupDate,
            @Param("dropoffDate") LocalDate dropoffDate,
            @Param("minCapacity") Integer minCapacity,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
    
    /**
     * Checks if a specific car is available for the requested date range.
     * Used for validation before creating a reservation.
     * 
     * @param carId Car ID to check
     * @param pickupDate Requested pickup date
     * @param dropoffDate Requested dropoff date
     * @return true if car is available, false if there are conflicting reservations
     */
    @Query("SELECT CASE WHEN COUNT(cr) = 0 THEN true ELSE false END " +
           "FROM CarRentalReservation cr " +
           "WHERE cr.carRental.id = :carId " +
           "AND cr.status IN ('CONFIRMED', 'PENDING') " +
           "AND cr.pickupDate <= :dropoffDate " +
           "AND cr.dropoffDate >= :pickupDate")
    Boolean isCarAvailableForDateRange(
            @Param("carId") Long carId,
            @Param("pickupDate") LocalDate pickupDate,
            @Param("dropoffDate") LocalDate dropoffDate);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    // Consider migrating service layer to use paginated versions above
    
    /**
     * @deprecated Use findByIsAvailableTrue(Pageable pageable) instead.
     * This method loads all records into memory which is not scalable.
     */
    @Deprecated
    List<CarRental> findByIsAvailableTrue();
    
    /**
     * @deprecated Use findByCarTypeAndIsAvailableTrue(String carType, Pageable pageable) instead.
     */
    @Deprecated
    List<CarRental> findByCarTypeAndIsAvailableTrue(String carType);
    
    /**
     * @deprecated Use findByPriceRange instead.
     */
    @Deprecated
    @Query("SELECT c FROM CarRental c WHERE c.isAvailable = true AND c.pricePerDay <= :maxPrice ORDER BY c.pricePerDay ASC")
    List<CarRental> findByPricePerDayLessThanEqual(@Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * @deprecated Use findAllAvailableOrderByRatingDesc(Pageable pageable) instead.
     */
    @Deprecated
    @Query("SELECT c FROM CarRental c WHERE c.isAvailable = true ORDER BY c.rating DESC")
    List<CarRental> findAllAvailableOrderByRatingDesc();
    
    /**
     * Basic find by ID and available status.
     */
    Optional<CarRental> findByIdAndIsAvailableTrue(Long id);
}

