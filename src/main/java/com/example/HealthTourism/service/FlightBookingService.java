package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.FlightBookingDTO;
import com.example.HealthTourism.entity.FlightBooking;
import com.example.HealthTourism.exception.FlightNotFoundException;
import com.example.HealthTourism.mapper.FlightBookingMapper;
import com.example.HealthTourism.repository.FlightBookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enterprise-grade FlightBookingService with:
 * - Constructor Injection for testability
 * - MapStruct for entity-DTO mapping
 * - Transactional management
 * - Capacity management (available seats control)
 * - Business logic validation (past date filtering, direct flight prioritization)
 * - Optimistic locking for concurrent seat booking
 * - Proper exception handling
 */
@Slf4j
@Service
@RequiredArgsConstructor // Constructor Injection - Spring best practice
@Transactional(readOnly = true) // Default: read-only for all methods (performance optimization)
public class FlightBookingService {
    
    private final FlightBookingRepository flightBookingRepository;
    private final FlightBookingMapper flightBookingMapper;

    /**
     * Gets all available flights.
     * Business Logic: Filters out past flights (only future flights are returned).
     * 
     * @return List of available future flights
     */
    public List<FlightBookingDTO> getAllAvailableFlights() {
        log.debug("Fetching all available flights");
        LocalDateTime now = LocalDateTime.now();
        
        return flightBookingRepository.findByIsAvailableTrue()
                .stream()
                .filter(f -> f.getDepartureDateTime().isAfter(now)) // Business rule: only future flights
                .map(flightBookingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Searches flights by departure and arrival cities.
     * Business Logic:
     * - Filters out past flights
     * - Prioritizes direct flights (health tourism preference)
     * - Only shows flights with available seats
     * 
     * @param departureCity Departure city
     * @param arrivalCity Arrival city
     * @return List of matching flights (direct flights first, then by departure time)
     */
    public List<FlightBookingDTO> searchFlights(String departureCity, String arrivalCity) {
        log.debug("Searching flights from {} to {}", departureCity, arrivalCity);
        
        // Business logic validation
        if (departureCity == null || departureCity.trim().isEmpty()) {
            throw new IllegalArgumentException("Departure city cannot be null or empty");
        }
        if (arrivalCity == null || arrivalCity.trim().isEmpty()) {
            throw new IllegalArgumentException("Arrival city cannot be null or empty");
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        return flightBookingRepository.findByDepartureCityAndArrivalCityAndIsAvailableTrue(departureCity, arrivalCity)
                .stream()
                .filter(f -> f.getDepartureDateTime().isAfter(now)) // Business rule: only future flights
                .filter(f -> f.getAvailableSeats() > 0) // Business rule: must have available seats
                .sorted(Comparator
                        .comparing(FlightBooking::getIsDirectFlight, Comparator.reverseOrder()) // Direct flights first
                        .thenComparing(FlightBooking::getDepartureDateTime)) // Then by departure time
                .map(flightBookingMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Advanced flight search with date range and passenger count.
     * Business Logic:
     * - Validates date range
     * - Checks seat availability for requested passengers
     * - Filters past flights
     * - Prioritizes direct flights
     * 
     * @param departureCity Departure city
     * @param arrivalCity Arrival city
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @param passengers Number of passengers
     * @return List of matching flights with sufficient capacity
     */
    public List<FlightBookingDTO> searchFlightsWithDateRange(
            String departureCity, 
            String arrivalCity,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Integer passengers) {
        log.debug("Searching flights from {} to {} between {} and {} for {} passengers", 
                departureCity, arrivalCity, startDate, endDate, passengers);
        
        // Business logic validation
        if (departureCity == null || departureCity.trim().isEmpty()) {
            throw new IllegalArgumentException("Departure city cannot be null or empty");
        }
        if (arrivalCity == null || arrivalCity.trim().isEmpty()) {
            throw new IllegalArgumentException("Arrival city cannot be null or empty");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        if (passengers == null || passengers <= 0) {
            throw new IllegalArgumentException("Passenger count must be greater than 0");
        }
        
        // Use repository method that checks capacity
        return flightBookingRepository.searchFlights(
                departureCity, arrivalCity, startDate, endDate, passengers)
                .stream()
                .filter(f -> f.getDepartureDateTime().isAfter(LocalDateTime.now())) // Additional safety check
                .sorted(Comparator
                        .comparing(FlightBooking::getIsDirectFlight, Comparator.reverseOrder()) // Direct flights first
                        .thenComparing(FlightBooking::getDepartureDateTime)) // Then by departure time
                .map(flightBookingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets flights filtered by flight class.
     * Business Logic: Filters out past flights and flights with no available seats.
     * 
     * @param flightClass Flight class (Economy, Business, First)
     * @return List of matching flights
     */
    public List<FlightBookingDTO> getFlightsByClass(String flightClass) {
        log.debug("Fetching flights by class: {}", flightClass);
        
        if (flightClass == null || flightClass.trim().isEmpty()) {
            throw new IllegalArgumentException("Flight class cannot be null or empty");
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        return flightBookingRepository.findByFlightClassAndIsAvailableTrue(flightClass)
                .stream()
                .filter(f -> f.getDepartureDateTime().isAfter(now)) // Business rule: only future flights
                .filter(f -> f.getAvailableSeats() > 0) // Business rule: must have available seats
                .map(flightBookingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets flights filtered by maximum price.
     * Business Logic: Validates price and filters past flights.
     * 
     * @param maxPrice Maximum price
     * @return List of flights within price range
     * @throws IllegalArgumentException if maxPrice is negative
     */
    public List<FlightBookingDTO> getFlightsByPrice(BigDecimal maxPrice) {
        log.debug("Fetching flights with max price: {}", maxPrice);
        
        // Business logic validation: Price cannot be negative
        if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maximum price must be non-negative. Provided: " + maxPrice);
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        return flightBookingRepository.findByPriceLessThanEqual(maxPrice)
                .stream()
                .filter(f -> f.getDepartureDateTime().isAfter(now)) // Business rule: only future flights
                .filter(f -> f.getAvailableSeats() > 0) // Business rule: must have available seats
                .sorted(Comparator
                        .comparing(FlightBooking::getIsDirectFlight, Comparator.reverseOrder()) // Direct flights first
                        .thenComparing(FlightBooking::getPrice)) // Then by price
                .map(flightBookingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets a single flight by ID.
     * 
     * @param id Flight ID
     * @return FlightBookingDTO
     * @throws FlightNotFoundException if flight not found or unavailable
     */
    public FlightBookingDTO getFlightById(Long id) {
        log.debug("Fetching flight with ID: {}", id);
        return flightBookingRepository.findByIdAndIsAvailableTrue(id)
                .map(flightBookingMapper::toDto)
                .orElseThrow(() -> new FlightNotFoundException(id));
    }

    /**
     * Health Tourism Critical: Reserves seats for a flight.
     * Business Logic:
     * - Validates flight exists and is available
     * - Checks seat availability (availableSeats >= count)
     * - Uses optimistic locking to prevent concurrent booking conflicts
     * - Atomically decreases available seats
     * - Updates isAvailable flag when seats run out
     * 
     * CRITICAL: This method uses repository's atomic bookSeats method with optimistic locking
     * to prevent race conditions when multiple users try to book the same seats simultaneously.
     * 
     * @param flightId Flight ID
     * @param count Number of seats to reserve
     * @throws FlightNotFoundException if flight not found
     * @throws IllegalStateException if insufficient seats available
     */
    @Transactional // Critical for write operation - ensures data consistency
    public void reserveSeat(Long flightId, int count) {
        log.info("Reserving {} seat(s) for flight ID: {}", count, flightId);
        
        // Business logic validation
        if (count <= 0) {
            throw new IllegalArgumentException("Seat count must be greater than 0");
        }
        
        // Check if flight exists
        FlightBooking flight = flightBookingRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));
        
        // Business logic validation: Flight must be available
        if (!flight.getIsAvailable()) {
            throw new IllegalStateException("Flight is not available for booking");
        }
        
        // Business logic validation: Check seat availability
        if (flight.getAvailableSeats() < count) {
            throw new IllegalStateException(
                    String.format("Yeterli boş koltuk yok! Mevcut: %d, İstenen: %d", 
                            flight.getAvailableSeats(), count));
        }
        
        // Business logic validation: Flight must be in the future
        if (flight.getDepartureDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot book seats for past flights");
        }
        
        // Use atomic booking method (optimistic locking)
        int updatedRows = flightBookingRepository.bookSeats(flightId, count);
        
        if (updatedRows == 0) {
            // Optimistic locking conflict or seats no longer available
            // Refresh entity to get current state
            flight = flightBookingRepository.findById(flightId)
                    .orElseThrow(() -> new FlightNotFoundException(flightId));
            
            if (flight.getAvailableSeats() < count) {
                throw new IllegalStateException(
                        String.format("Yeterli boş koltuk yok! Mevcut: %d, İstenen: %d. " +
                                "Başka bir kullanıcı bu uçuştan koltuk rezerve etmiş olabilir.", 
                                flight.getAvailableSeats(), count));
            } else {
                throw new IllegalStateException(
                        "Koltuk rezervasyonu başarısız. Lütfen tekrar deneyin.");
            }
        }
        
        // Check if flight should be marked as unavailable (no seats left)
        flight = flightBookingRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));
        
        if (flight.getAvailableSeats() == 0) {
            flight.setIsAvailable(false);
            flightBookingRepository.save(flight);
            log.info("Flight {} marked as unavailable (no seats left)", flightId);
        }
        
        log.info("Successfully reserved {} seat(s) for flight ID: {}", count, flightId);
    }
    
    /**
     * Releases seats (cancellation scenario).
     * Business Logic:
     * - Atomically increases available seats
     * - Re-enables flight if it was marked unavailable
     * 
     * @param flightId Flight ID
     * @param count Number of seats to release
     */
    @Transactional
    public void releaseSeats(Long flightId, int count) {
        log.info("Releasing {} seat(s) for flight ID: {}", count, flightId);
        
        if (count <= 0) {
            throw new IllegalArgumentException("Seat count must be greater than 0");
        }
        
        FlightBooking flight = flightBookingRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));
        
        // Use atomic release method
        int updatedRows = flightBookingRepository.releaseSeats(flightId, count);
        
        if (updatedRows == 0) {
            throw new IllegalStateException("Failed to release seats. Flight may not be available.");
        }
        
        // Re-enable flight if it was marked unavailable
        flight = flightBookingRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));
        
        if (!flight.getIsAvailable() && flight.getAvailableSeats() > 0) {
            flight.setIsAvailable(true);
            flightBookingRepository.save(flight);
            log.info("Flight {} re-enabled (seats available)", flightId);
        }
        
        log.info("Successfully released {} seat(s) for flight ID: {}", count, flightId);
    }
    
    /**
     * Gets direct flights only (health tourism preference).
     * Business Logic: Filters for direct flights, future dates, and available seats.
     * 
     * @param departureCity Departure city
     * @param arrivalCity Arrival city
     * @return List of direct flights
     */
    public List<FlightBookingDTO> getDirectFlights(String departureCity, String arrivalCity) {
        log.debug("Fetching direct flights from {} to {}", departureCity, arrivalCity);
        
        LocalDateTime now = LocalDateTime.now();
        
        return flightBookingRepository.findByDepartureCityAndArrivalCityAndIsAvailableTrue(departureCity, arrivalCity)
                .stream()
                .filter(FlightBooking::getIsDirectFlight) // Direct flights only
                .filter(f -> f.getDepartureDateTime().isAfter(now)) // Future flights only
                .filter(f -> f.getAvailableSeats() > 0) // Must have available seats
                .sorted(Comparator.comparing(FlightBooking::getDepartureDateTime)) // Sort by departure time
                .map(flightBookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
