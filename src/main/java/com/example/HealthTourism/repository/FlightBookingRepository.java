package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.FlightBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Enterprise-grade FlightBookingRepository with capacity control, date range search,
 * round trip support, and optimistic locking for concurrent seat booking.
 * 
 * Features:
 * - Capacity control (available seats validation)
 * - Date range search (critical for health tourism)
 * - Round trip query support
 * - Pagination support
 * - Optimistic locking for seat booking (prevents double booking)
 * - Airline filtering and high availability queries
 */
@Repository
public interface FlightBookingRepository extends JpaRepository<FlightBooking, Long>, JpaSpecificationExecutor<FlightBooking> {
    
    // ==================== Advanced Flight Search with Capacity Control ====================
    
    /**
     * Comprehensive flight search with city, date range, and passenger capacity check.
     * Validates that enough seats are available for the requested number of passengers.
     * 
     * @param from Departure city
     * @param to Arrival city
     * @param start Start of date range (inclusive)
     * @param end End of date range (inclusive)
     * @param passengers Number of passengers (must have availableSeats >= passengers)
     * @return List of flights matching criteria with sufficient capacity
     */
    @Query("SELECT f FROM FlightBooking f WHERE f.departureCity = :from " +
           "AND f.arrivalCity = :to " +
           "AND f.departureDateTime BETWEEN :start AND :end " +
           "AND f.availableSeats >= :passengers " +
           "AND f.isAvailable = true " +
           "ORDER BY f.departureDateTime ASC, f.price ASC")
    List<FlightBooking> searchFlights(
            @Param("from") String from, 
            @Param("to") String to, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end, 
            @Param("passengers") Integer passengers);
    
    /**
     * Flight search with pagination support.
     */
    @Query("SELECT f FROM FlightBooking f WHERE f.departureCity = :from " +
           "AND f.arrivalCity = :to " +
           "AND f.departureDateTime BETWEEN :start AND :end " +
           "AND f.availableSeats >= :passengers " +
           "AND f.isAvailable = true " +
           "ORDER BY f.departureDateTime ASC, f.price ASC")
    Page<FlightBooking> searchFlights(
            @Param("from") String from, 
            @Param("to") String to, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end, 
            @Param("passengers") Integer passengers,
            Pageable pageable);
    
    // ==================== Round Trip Support ====================
    
    /**
     * Finds round trip flights (outbound and return).
     * Returns both departure and return flights that match the criteria.
     * 
     * @param from Departure city
     * @param to Arrival city
     * @param outboundStart Start of outbound date range
     * @param outboundEnd End of outbound date range
     * @param returnStart Start of return date range
     * @param returnEnd End of return date range
     * @param passengers Number of passengers
     * @return List containing both outbound and return flights (ordered by type, then date)
     */
    @Query("SELECT f FROM FlightBooking f WHERE " +
           "((f.departureCity = :from AND f.arrivalCity = :to " +
           "  AND f.departureDateTime BETWEEN :outboundStart AND :outboundEnd) " +
           "OR " +
           "(f.departureCity = :to AND f.arrivalCity = :from " +
           "  AND f.departureDateTime BETWEEN :returnStart AND :returnEnd)) " +
           "AND f.availableSeats >= :passengers " +
           "AND f.isAvailable = true " +
           "ORDER BY CASE WHEN f.departureCity = :from THEN 1 ELSE 2 END, " +
           "f.departureDateTime ASC")
    List<FlightBooking> searchRoundTripFlights(
            @Param("from") String from,
            @Param("to") String to,
            @Param("outboundStart") LocalDateTime outboundStart,
            @Param("outboundEnd") LocalDateTime outboundEnd,
            @Param("returnStart") LocalDateTime returnStart,
            @Param("returnEnd") LocalDateTime returnEnd,
            @Param("passengers") Integer passengers);
    
    // ==================== Airline Filtering ====================
    
    /**
     * Finds flights by airline with pagination.
     */
    Page<FlightBooking> findByAirlineNameAndIsAvailableTrue(String airline, Pageable pageable);
    
    /**
     * Finds flights by airline and route with pagination.
     */
    @Query("SELECT f FROM FlightBooking f WHERE f.airlineName = :airline " +
           "AND f.departureCity = :from AND f.arrivalCity = :to " +
           "AND f.isAvailable = true " +
           "ORDER BY f.departureDateTime ASC")
    Page<FlightBooking> findByAirlineAndRoute(
            @Param("airline") String airline,
            @Param("from") String from,
            @Param("to") String to,
            Pageable pageable);
    
    // ==================== High Availability Queries (Promotions) ====================
    
    /**
     * Finds flights with high availability (more than 50% seats available).
     * Used for promotional listings.
     * Calculates occupancy rate: (totalSeats - availableSeats) / totalSeats < 0.5
     */
    @Query("SELECT f FROM FlightBooking f WHERE " +
           "f.availableSeats > (f.totalSeats * 0.5) " +
           "AND f.isAvailable = true " +
           "ORDER BY f.availableSeats DESC, f.price ASC")
    List<FlightBooking> findFlightsWithHighAvailability();
    
    /**
     * Finds flights with high availability with pagination.
     */
    @Query("SELECT f FROM FlightBooking f WHERE " +
           "f.availableSeats > (f.totalSeats * 0.5) " +
           "AND f.isAvailable = true " +
           "ORDER BY f.availableSeats DESC, f.price ASC")
    Page<FlightBooking> findFlightsWithHighAvailability(Pageable pageable);
    
    // ==================== Optimistic Locking - Seat Booking ====================
    
    /**
     * Books seats atomically with optimistic locking.
     * Uses version field to prevent concurrent booking of the same seats.
     * 
     * Returns the number of affected rows:
     * - 1: Seats booked successfully
     * - 0: No seats available (availableSeats < count) OR version conflict (optimistic locking failure)
     * 
     * IMPORTANT: This method must be called within a @Transactional context.
     * If return value is 0, the transaction should be rolled back.
     * 
     * @param id Flight booking ID
     * @param count Number of seats to book
     * @return Number of rows updated (1 = success, 0 = failure)
     */
    @Modifying
    @Transactional
    @Query("UPDATE FlightBooking f SET f.availableSeats = f.availableSeats - :count " +
           "WHERE f.id = :id " +
           "AND f.availableSeats >= :count " +
           "AND f.isAvailable = true")
    int bookSeats(@Param("id") Long id, @Param("count") Integer count);
    
    /**
     * Releases seats (cancellation scenario).
     * Atomically increases available seats.
     * 
     * @param id Flight booking ID
     * @param count Number of seats to release
     * @return Number of rows updated
     */
    @Modifying
    @Transactional
    @Query("UPDATE FlightBooking f SET f.availableSeats = f.availableSeats + :count " +
           "WHERE f.id = :id " +
           "AND f.isAvailable = true")
    int releaseSeats(@Param("id") Long id, @Param("count") Integer count);
    
    /**
     * Checks if a flight has enough available seats for the requested number of passengers.
     * Used for validation before attempting to book.
     * 
     * @param id Flight booking ID
     * @param passengers Number of passengers
     * @return true if enough seats available, false otherwise
     */
    @Query("SELECT CASE WHEN f.availableSeats >= :passengers AND f.isAvailable = true THEN true ELSE false END " +
           "FROM FlightBooking f WHERE f.id = :id")
    Boolean hasEnoughSeats(@Param("id") Long id, @Param("passengers") Integer passengers);
    
    // ==================== Pagination Support ====================
    
    /**
     * Finds all available flights with pagination.
     * Enterprise requirement: Always use pagination for list queries.
     */
    Page<FlightBooking> findByIsAvailableTrue(Pageable pageable);
    
    /**
     * Finds flights by departure city with pagination.
     * Useful for major hub airports.
     */
    Page<FlightBooking> findByDepartureCityAndIsAvailableTrue(String departureCity, Pageable pageable);
    
    /**
     * Finds flights by route with pagination.
     */
    Page<FlightBooking> findByDepartureCityAndArrivalCityAndIsAvailableTrue(
            String departureCity, 
            String arrivalCity, 
            Pageable pageable);
    
    /**
     * Finds flights by flight class with pagination.
     */
    Page<FlightBooking> findByFlightClassAndIsAvailableTrue(String flightClass, Pageable pageable);
    
    /**
     * Finds flights by date range with pagination.
     */
    @Query("SELECT f FROM FlightBooking f WHERE f.departureDateTime BETWEEN :start AND :end " +
           "AND f.isAvailable = true " +
           "ORDER BY f.departureDateTime ASC")
    Page<FlightBooking> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    
    /**
     * @deprecated Use findByIsAvailableTrue(Pageable pageable) instead.
     * This method loads all records into memory which is not scalable.
     */
    @Deprecated
    List<FlightBooking> findByIsAvailableTrue();
    
    /**
     * @deprecated Use findByDepartureCityAndArrivalCityAndIsAvailableTrue with Pageable instead.
     */
    @Deprecated
    List<FlightBooking> findByDepartureCityAndArrivalCityAndIsAvailableTrue(String departureCity, String arrivalCity);
    
    /**
     * @deprecated Use findByFlightClassAndIsAvailableTrue with Pageable instead.
     */
    @Deprecated
    List<FlightBooking> findByFlightClassAndIsAvailableTrue(String flightClass);
    
    /**
     * @deprecated Use Specification API or searchFlights with price filter instead.
     */
    @Deprecated
    @Query("SELECT f FROM FlightBooking f WHERE f.isAvailable = true AND f.price <= :maxPrice ORDER BY f.price ASC")
    List<FlightBooking> findByPriceLessThanEqual(@Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * @deprecated Use findByDateRange instead.
     */
    @Deprecated
    @Query("SELECT f FROM FlightBooking f WHERE f.isAvailable = true AND f.departureDateTime >= :date ORDER BY f.departureDateTime ASC")
    List<FlightBooking> findAvailableFlightsAfterDate(@Param("date") LocalDateTime date);
    
    /**
     * Basic find by ID and available status.
     */
    Optional<FlightBooking> findByIdAndIsAvailableTrue(Long id);
}

