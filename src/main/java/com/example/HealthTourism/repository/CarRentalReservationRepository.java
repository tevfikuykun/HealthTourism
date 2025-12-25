package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.CarRentalReservation;
import com.example.HealthTourism.repository.projection.ReservationSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Enterprise-grade CarRentalReservationRepository with status-based filtering,
 * pagination support, JOIN FETCH optimization, and admin panel queries.
 * 
 * Features:
 * - Status-based filtering with pagination
 * - JOIN FETCH for N+1 query prevention
 * - Date conflict checking for double booking prevention
 * - Admin panel operational queries
 * - Interface-based projections for lightweight queries
 */
@Repository
public interface CarRentalReservationRepository extends JpaRepository<CarRentalReservation, Long> {
    
    // ==================== User Queries with Status Filtering ====================
    
    /**
     * Finds user's reservations by status with pagination support.
     * Allows filtering by status: PENDING, CONFIRMED, CANCELLED, COMPLETED
     */
    Page<CarRentalReservation> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    
    /**
     * Finds all user reservations with pagination.
     */
    Page<CarRentalReservation> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Finds user's active reservations (CONFIRMED or PENDING) with pagination.
     */
    @Query("SELECT cr FROM CarRentalReservation cr " +
           "WHERE cr.user.id = :userId " +
           "AND cr.status IN ('CONFIRMED', 'PENDING') " +
           "ORDER BY cr.pickupDate ASC")
    Page<CarRentalReservation> findActiveReservationsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Finds user's completed reservations with pagination.
     */
    Page<CarRentalReservation> findByUserIdAndStatusOrderByDropoffDateDesc(Long userId, String status, Pageable pageable);
    
    // ==================== Performance-optimized queries with JOIN FETCH ====================
    
    /**
     * Finds reservation by number with all related entities eagerly fetched (prevents N+1).
     * Use this when you need complete reservation details including car and user data.
     */
    @Query("SELECT r FROM CarRentalReservation r " +
           "JOIN FETCH r.carRental " +
           "JOIN FETCH r.user " +
           "WHERE r.reservationNumber = :resNum")
    Optional<CarRentalReservation> findByReservationNumberWithDetails(@Param("resNum") String reservationNumber);
    
    /**
     * Finds reservation by ID with all related entities eagerly fetched.
     */
    @Query("SELECT r FROM CarRentalReservation r " +
           "JOIN FETCH r.carRental " +
           "JOIN FETCH r.user " +
           "WHERE r.id = :id")
    Optional<CarRentalReservation> findByIdWithDetails(@Param("id") Long id);
    
    /**
     * Finds user's reservations with car and user data eagerly fetched.
     */
    @Query(value = "SELECT r FROM CarRentalReservation r " +
           "JOIN FETCH r.carRental " +
           "JOIN FETCH r.user " +
           "WHERE r.user.id = :userId",
           countQuery = "SELECT COUNT(r) FROM CarRentalReservation r WHERE r.user.id = :userId")
    Page<CarRentalReservation> findByUserIdWithDetails(@Param("userId") Long userId, Pageable pageable);
    
    // ==================== Date Conflict Checking (Double Booking Prevention) ====================
    
    /**
     * Checks if there are any overlapping reservations for a car in the specified date range.
     * Critical query for preventing double booking.
     * 
     * Date overlap logic: Two date ranges overlap if:
     * - (pickupDate <= requestedDropoffDate) AND (dropoffDate >= requestedPickupDate)
     * 
     * @param carId Car rental ID
     * @param pickupDate Start of the requested date range
     * @param dropoffDate End of the requested date range
     * @return true if overlapping reservation exists, false otherwise
     */
    @Query("SELECT COUNT(r) > 0 FROM CarRentalReservation r " +
           "WHERE r.carRental.id = :carId " +
           "AND r.status = 'CONFIRMED' " +
           "AND (:pickupDate <= r.dropoffDate AND :dropoffDate >= r.pickupDate)")
    boolean existsOverlappingReservation(
            @Param("carId") Long carId, 
            @Param("pickupDate") LocalDate pickupDate, 
            @Param("dropoffDate") LocalDate dropoffDate);
    
    /**
     * Finds all active reservations for a specific car within a date range.
     * Used to check if a car has conflicting reservations.
     */
    @Query("SELECT cr FROM CarRentalReservation cr " +
           "WHERE cr.carRental.id = :carId " +
           "AND cr.status IN ('CONFIRMED', 'PENDING') " +
           "AND cr.pickupDate <= :dropoffDate " +
           "AND cr.dropoffDate >= :pickupDate " +
           "ORDER BY cr.pickupDate ASC")
    List<CarRentalReservation> findConflictingReservations(
            @Param("carId") Long carId,
            @Param("pickupDate") LocalDate pickupDate,
            @Param("dropoffDate") LocalDate dropoffDate);
    
    /**
     * Checks if a car is available for the specified date range.
     * Returns true if car is available (no conflicts), false if there are conflicts.
     */
    @Query("SELECT CASE WHEN COUNT(cr) = 0 THEN true ELSE false END " +
           "FROM CarRentalReservation cr " +
           "WHERE cr.carRental.id = :carId " +
           "AND cr.status IN ('CONFIRMED', 'PENDING') " +
           "AND cr.pickupDate <= :dropoffDate " +
           "AND cr.dropoffDate >= :pickupDate")
    Boolean isCarAvailableForDates(
            @Param("carId") Long carId,
            @Param("pickupDate") LocalDate pickupDate,
            @Param("dropoffDate") LocalDate dropoffDate);
    
    // ==================== Admin Panel Queries ====================
    
    /**
     * Finds reservations that start (pickup) today with a specific status.
     * Used for operational tracking - preparing cars for today's pickups.
     */
    @Query("SELECT r FROM CarRentalReservation r " +
           "JOIN FETCH r.carRental " +
           "JOIN FETCH r.user " +
           "WHERE r.pickupDate = :date " +
           "AND r.status = :status " +
           "ORDER BY r.pickupDate ASC")
    List<CarRentalReservation> findByPickupDateAndStatus(
            @Param("date") LocalDate date, 
            @Param("status") String status);
    
    /**
     * Finds today's pickups (CONFIRMED status) - operational query.
     */
    @Query("SELECT r FROM CarRentalReservation r " +
           "JOIN FETCH r.carRental " +
           "JOIN FETCH r.user " +
           "WHERE r.pickupDate = CURRENT_DATE " +
           "AND r.status = 'CONFIRMED' " +
           "ORDER BY r.pickupDate ASC")
    List<CarRentalReservation> findTodayPickups();
    
    /**
     * Finds overdue returns - cars that should have been returned but dropoffDate has passed.
     * Critical for fleet management.
     */
    @Query("SELECT r FROM CarRentalReservation r " +
           "JOIN FETCH r.carRental " +
           "JOIN FETCH r.user " +
           "WHERE r.dropoffDate < CURRENT_DATE " +
           "AND r.status IN ('CONFIRMED', 'PENDING') " +
           "ORDER BY r.dropoffDate ASC")
    List<CarRentalReservation> findOverdueReturns();
    
    /**
     * Finds upcoming pickups within the next N days (for planning).
     */
    @Query("SELECT r FROM CarRentalReservation r " +
           "JOIN FETCH r.carRental " +
           "JOIN FETCH r.user " +
           "WHERE r.pickupDate BETWEEN CURRENT_DATE AND :endDate " +
           "AND r.status = 'CONFIRMED' " +
           "ORDER BY r.pickupDate ASC")
    List<CarRentalReservation> findUpcomingPickups(@Param("endDate") LocalDate endDate);
    
    /**
     * Finds reservations by status with pagination (admin dashboard).
     */
    Page<CarRentalReservation> findByStatus(String status, Pageable pageable);
    
    /**
     * Finds reservations within a date range with pagination (admin reports).
     */
    @Query("SELECT r FROM CarRentalReservation r " +
           "WHERE r.pickupDate BETWEEN :startDate AND :endDate " +
           "ORDER BY r.pickupDate DESC")
    Page<CarRentalReservation> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
    
    // ==================== Interface-based Projections (Lightweight Queries) ====================
    
    /**
     * Returns reservation summaries for a user.
     * Uses interface projection with @Value for nested properties.
     * This loads only ReservationSummary interface fields, not full entities.
     * 
     * Note: The projection interface uses @Value("#{target.carRental.carModel}") 
     * to access nested properties from the joined CarRental entity.
     */
    @Query("SELECT r FROM CarRentalReservation r " +
           "LEFT JOIN FETCH r.carRental " +
           "WHERE r.user.id = :userId " +
           "ORDER BY r.pickupDate DESC")
    List<ReservationSummary> findSummaryByUserId(@Param("userId") Long userId);
    
    /**
     * Returns reservation summaries by user and status.
     * Uses interface projection for lightweight queries.
     */
    @Query("SELECT r FROM CarRentalReservation r " +
           "LEFT JOIN FETCH r.carRental " +
           "WHERE r.user.id = :userId AND r.status = :status " +
           "ORDER BY r.pickupDate DESC")
    List<ReservationSummary> findSummaryByUserIdAndStatus(
            @Param("userId") Long userId, 
            @Param("status") String status);
    
    // ==================== Car-specific Queries ====================
    
    /**
     * Finds all reservations for a specific car (all statuses) with pagination.
     */
    Page<CarRentalReservation> findByCarRentalId(Long carId, Pageable pageable);
    
    /**
     * Finds active reservations (CONFIRMED or PENDING) for a specific car.
     */
    @Query("SELECT cr FROM CarRentalReservation cr " +
           "WHERE cr.carRental.id = :carId " +
           "AND cr.status IN ('CONFIRMED', 'PENDING') " +
           "ORDER BY cr.pickupDate ASC")
    List<CarRentalReservation> findActiveReservationsByCarId(@Param("carId") Long carId);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    
    /**
     * Basic find by reservation number (without JOIN FETCH).
     * For better performance, use findByReservationNumberWithDetails instead.
     */
    Optional<CarRentalReservation> findByReservationNumber(String reservationNumber);
    
    /**
     * @deprecated Use findByUserId(Long userId, Pageable pageable) instead.
     * This method loads all records into memory which is not scalable.
     */
    @Deprecated
    List<CarRentalReservation> findByUserId(Long userId);
    
    /**
     * @deprecated Use findByCarRentalId(Long carId, Pageable pageable) instead.
     */
    @Deprecated
    List<CarRentalReservation> findByCarRentalId(Long carId);
}

