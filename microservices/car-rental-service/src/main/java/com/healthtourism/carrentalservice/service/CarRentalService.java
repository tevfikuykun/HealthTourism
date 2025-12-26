package com.healthtourism.carrentalservice.service;

import com.healthtourism.carrentalservice.dto.CarRentalDTO;
import com.healthtourism.carrentalservice.entity.CarRental;
import com.healthtourism.carrentalservice.exception.CarRentalNotFoundException;
import com.healthtourism.carrentalservice.mapper.CarRentalMapper;
import com.healthtourism.carrentalservice.repository.CarRentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enterprise-grade CarRentalService with:
 * - Constructor Injection for testability
 * - MapStruct for entity-DTO mapping
 * - Transactional management
 * - Business logic validation
 * - Dynamic pricing (driver inclusion)
 * - Health tourism specific methods
 * - Proper exception handling
 */
@Slf4j
@Service
@RequiredArgsConstructor // Constructor Injection - Spring best practice
public class CarRentalService {

    private final CarRentalRepository carRentalRepository;
    private final CarRentalMapper carRentalMapper;

    // Business constants for health tourism pricing
    private static final BigDecimal DRIVER_SURCHARGE_PERCENTAGE = new BigDecimal("0.30"); // 30% surcharge for driver
    private static final BigDecimal DRIVER_FIXED_FEE = new BigDecimal("50.00"); // Fixed daily driver fee

    /**
     * Gets all available car rentals.
     */
    public List<CarRentalDTO> getAllAvailableCarRentals() {
        log.debug("Fetching all available car rentals");
        return carRentalRepository.findByIsAvailableTrue()
                .stream()
                .map(carRentalMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets car rentals filtered by car type.
     * 
     * @param carType Car type (Economy, Standard, Luxury, SUV, Van)
     * @return List of car rentals matching the type
     */
    public List<CarRentalDTO> getCarRentalsByType(String carType) {
        log.debug("Fetching car rentals by type: {}", carType);
        if (carType == null || carType.trim().isEmpty()) {
            throw new IllegalArgumentException("Car type cannot be null or empty");
        }
        
        return carRentalRepository.findByCarTypeAndIsAvailableTrue(carType)
                .stream()
                .map(carRentalMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets car rentals filtered by maximum price per day.
     * Business Logic: Validates that maxPrice is not negative.
     * 
     * @param maxPrice Maximum price per day (must be non-negative)
     * @return List of car rentals within price range
     * @throws IllegalArgumentException if maxPrice is negative
     */
    public List<CarRentalDTO> getCarRentalsByPrice(BigDecimal maxPrice) {
        log.debug("Fetching car rentals with max price: {}", maxPrice);
        
        // Business logic validation: Price cannot be negative
        if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maximum price must be non-negative. Provided: " + maxPrice);
        }
        
        return carRentalRepository.findByPricePerDayLessThanEqual(maxPrice)
                .stream()
                .map(carRentalMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets car rentals with dynamic pricing based on driver inclusion.
     * Business Logic: If includesDriver is true, price is adjusted:
     * - Base price + (base price * 30%) + fixed daily driver fee (50)
     * This is critical for health tourism packages.
     * 
     * @param maxPrice Maximum base price (before driver surcharge)
     * @param includesDriver Whether to include driver service
     * @return List of car rentals with adjusted prices if driver is included
     */
    public List<CarRentalDTO> getCarRentalsByPriceWithDriver(BigDecimal maxPrice, Boolean includesDriver) {
        log.debug("Fetching car rentals with max price: {} and driver inclusion: {}", maxPrice, includesDriver);
        
        // Business logic validation
        if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maximum price must be non-negative. Provided: " + maxPrice);
        }
        
        if (includesDriver == null) {
            includesDriver = false;
        }
        
        List<CarRental> rentals = carRentalRepository.findByIsAvailableTrue();
        
        return rentals.stream()
                .filter(car -> {
                    BigDecimal effectivePrice = calculateEffectivePrice(car, includesDriver);
                    return effectivePrice.compareTo(maxPrice) <= 0;
                })
                .map(car -> {
                    CarRentalDTO dto = carRentalMapper.toDto(car);
                    // Adjust price in DTO if driver is included
                    if (includesDriver) {
                        BigDecimal adjustedPrice = calculateEffectivePrice(car, true);
                        dto.setPricePerDay(adjustedPrice);
                        log.debug("Adjusted price for car {}: {} (includes driver)", 
                                car.getCarModel(), adjustedPrice);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Business Logic: Calculates effective price including driver surcharge if applicable.
     * Formula: basePrice + (basePrice * 30%) + 50 (fixed driver fee)
     * 
     * @param carRental Car rental entity
     * @param includesDriver Whether driver service is included
     * @return Effective daily price
     */
    private BigDecimal calculateEffectivePrice(CarRental carRental, Boolean includesDriver) {
        BigDecimal basePrice = carRental.getPricePerDay();
        
        if (!includesDriver || !carRental.getIncludesDriver()) {
            return basePrice;
        }
        
        // Driver surcharge: 30% of base price + fixed fee
        BigDecimal surcharge = basePrice.multiply(DRIVER_SURCHARGE_PERCENTAGE)
                .add(DRIVER_FIXED_FEE);
        
        return basePrice.add(surcharge)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Health Tourism Specific: Gets car rentals suitable for health tourists.
     * Filters cars based on passenger capacity and luggage capacity.
     * Business Logic: Health tourists often travel with family and medical equipment,
     * so capacity requirements are critical.
     * 
     * @param patientCount Number of patients/passengers
     * @param luggageCount Number of luggage pieces (including medical equipment)
     * @return List of suitable car rentals
     * @throws IllegalArgumentException if capacities are invalid
     */
    public List<CarRentalDTO> getCarRentalsForHealthTourist(int patientCount, int luggageCount) {
        log.info("Searching cars for health tourist - passengers: {}, luggage: {}", 
                patientCount, luggageCount);
        
        // Business logic validation
        if (patientCount <= 0) {
            throw new IllegalArgumentException("Patient count must be greater than 0");
        }
        if (luggageCount < 0) {
            throw new IllegalArgumentException("Luggage count cannot be negative");
        }
        
        return carRentalRepository.findByIsAvailableTrue()
                .stream()
                .filter(car -> car.getPassengerCapacity() >= patientCount)
                .filter(car -> car.getLuggageCapacity() >= luggageCount)
                .sorted((a, b) -> {
                    // Sort by capacity match (prefer cars with minimal excess capacity)
                    int capacityDiffA = Math.abs(a.getPassengerCapacity() - patientCount) + 
                                       Math.abs(a.getLuggageCapacity() - luggageCount);
                    int capacityDiffB = Math.abs(b.getPassengerCapacity() - patientCount) + 
                                       Math.abs(b.getLuggageCapacity() - luggageCount);
                    return Integer.compare(capacityDiffA, capacityDiffB);
                })
                .map(carRentalMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets a single car rental by ID.
     * 
     * @param id Car rental ID
     * @return CarRentalDTO
     * @throws CarRentalNotFoundException if car rental not found or unavailable
     */
    @Transactional(readOnly = true)
    public CarRentalDTO getCarRentalById(Long id) {
        log.debug("Fetching car rental with ID: {}", id);
        return carRentalRepository.findByIdAndIsAvailableTrue(id)
                .map(carRentalMapper::toDto)
                .orElseThrow(() -> new CarRentalNotFoundException(id));
    }
}
