package com.healthtourism.carrentalservice.repository;
import com.healthtourism.carrentalservice.entity.CarRental;
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
 * Enterprise-grade CarRentalRepository with pagination support,
 * capacity-based filtering, price range search, and dynamic query capabilities.
 * 
 * Note: This microservice version may not have direct access to CarRentalReservation
 * entity. Date conflict checking should be handled via reservation service API.
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
     */
    Page<CarRental> findByPickupLocationContainingIgnoreCaseAndIsAvailableTrue(String location, Pageable pageable);
    
    /**
     * Finds top-rated available cars with pagination.
     */
    @Query("SELECT c FROM CarRental c WHERE c.isAvailable = true " +
           "ORDER BY c.rating DESC, c.totalReviews DESC")
    Page<CarRental> findAllAvailableOrderByRatingDesc(Pageable pageable);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    
    /**
     * @deprecated Use findByIsAvailableTrue(Pageable pageable) instead.
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

