package com.healthtourism.accommodationservice.service;

import com.healthtourism.accommodationservice.dto.AccommodationDTO;
import com.healthtourism.accommodationservice.entity.Accommodation;
import com.healthtourism.accommodationservice.exception.AccommodationNotFoundException;
import com.healthtourism.accommodationservice.mapper.AccommodationMapper;
import com.healthtourism.accommodationservice.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enterprise-grade AccommodationService with:
 * - Constructor Injection for testability
 * - MapStruct for entity-DTO mapping
 * - Transactional management
 * - Business logic validation
 * - Proper exception handling
 */
@Slf4j
@Service
@RequiredArgsConstructor // Constructor Injection - Spring best practice
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;

    /**
     * Gets all active accommodations for a hospital.
     * Note: Consider using pagination for large datasets.
     */
    public List<AccommodationDTO> getAccommodationsByHospital(Long hospitalId) {
        log.debug("Fetching accommodations for hospital ID: {}", hospitalId);
        return accommodationRepository.findByHospitalIdAndIsActiveTrue(hospitalId)
                .stream()
                .map(accommodationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets accommodations near a hospital, ordered by distance.
     * Business Logic: Filters accommodations within reasonable distance (max 50km for health tourism).
     */
    public List<AccommodationDTO> getAccommodationsNearHospital(Long hospitalId) {
        log.debug("Fetching nearby accommodations for hospital ID: {}", hospitalId);
        final double MAX_REASONABLE_DISTANCE_KM = 50.0; // Business rule: health tourism requirement
        
        return accommodationRepository.findByHospitalIdOrderByDistanceAsc(hospitalId)
                .stream()
                .filter(acc -> acc.getDistanceToHospital() <= MAX_REASONABLE_DISTANCE_KM)
                .map(accommodationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets accommodations filtered by maximum price per night.
     * Business Logic: Validates that maxPrice is not negative.
     * 
     * @param maxPrice Maximum price per night (must be non-negative)
     * @return List of accommodations within price range
     * @throws IllegalArgumentException if maxPrice is negative
     */
    public List<AccommodationDTO> getAccommodationsByPrice(BigDecimal maxPrice) {
        log.debug("Fetching accommodations with max price: {}", maxPrice);
        
        // Business logic validation: Price cannot be negative
        if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maximum price must be non-negative. Provided: " + maxPrice);
        }
        
        return accommodationRepository.findByPricePerNightLessThanEqual(maxPrice)
                .stream()
                .map(accommodationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets top-rated accommodations for a hospital.
     */
    public List<AccommodationDTO> getTopRatedAccommodationsByHospital(Long hospitalId) {
        log.debug("Fetching top-rated accommodations for hospital ID: {}", hospitalId);
        return accommodationRepository.findByHospitalIdOrderByRatingDesc(hospitalId)
                .stream()
                .map(accommodationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets a single accommodation by ID.
     * 
     * @param id Accommodation ID
     * @return AccommodationDTO
     * @throws AccommodationNotFoundException if accommodation not found or inactive
     */
    public AccommodationDTO getAccommodationById(Long id) {
        log.debug("Fetching accommodation with ID: {}", id);
        return accommodationRepository.findByIdAndIsActiveTrue(id)
                .map(accommodationMapper::toDto)
                .orElseThrow(() -> new AccommodationNotFoundException(id));
    }

    /**
     * Creates a new accommodation.
     * Business Logic:
     * - Sets default values (isActive=true, rating=0, totalReviews=0)
     * - Validates business rules (price, distance, etc.)
     * 
     * @param accommodation Accommodation entity to create
     * @return Created AccommodationDTO
     * @throws IllegalArgumentException if business validation fails
     */
    @Transactional // Critical for write operations - ensures data consistency
    public AccommodationDTO createAccommodation(Accommodation accommodation) {
        log.info("Creating new accommodation: {}", accommodation.getName());
        
        // Business logic: Set default values
        accommodation.setIsActive(true);
        accommodation.setRating(0.0);
        accommodation.setTotalReviews(0);
        
        // Business logic validation: Price validation
        if (accommodation.getPricePerNight() != null && 
            accommodation.getPricePerNight().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price per night cannot be negative");
        }
        
        // Business logic validation: Distance validation
        // For health tourism, accommodations should be reasonably close to hospital
        if (accommodation.getDistanceToHospital() != null && 
            accommodation.getDistanceToHospital() > 50.0) {
            log.warn("Accommodation {} is {} km from hospital - may not be suitable for health tourism", 
                    accommodation.getName(), accommodation.getDistanceToHospital());
            // Warning only - not blocking, but logged for business decision
        }
        
        Accommodation saved = accommodationRepository.save(accommodation);
        log.info("Successfully created accommodation with ID: {}", saved.getId());
        
        return accommodationMapper.toDto(saved);
    }
}