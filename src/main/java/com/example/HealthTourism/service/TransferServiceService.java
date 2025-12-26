package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.TransferServiceDTO;
import com.example.HealthTourism.entity.TransferService;
import com.example.HealthTourism.exception.TransferNotFoundException;
import com.example.HealthTourism.mapper.TransferServiceMapper;
import com.example.HealthTourism.repository.TransferServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enterprise-grade TransferServiceService with:
 * - Constructor Injection for testability
 * - MapStruct for entity-DTO mapping
 * - Transactional management
 * - Dynamic location matching (pickup and dropoff)
 * - Capacity-based filtering (critical for health tourism)
 * - VIP service filtering (meet & greet)
 * - Advanced search capabilities
 * - Proper exception handling
 */
@Slf4j
@Service
@RequiredArgsConstructor // Constructor Injection - Spring best practice
@Transactional(readOnly = true) // Default: read-only for all methods (performance optimization)
public class TransferServiceService {
    
    private final TransferServiceRepository transferServiceRepository;
    private final TransferServiceMapper transferServiceMapper;

    /**
     * Gets all available transfer services.
     * Performance: Consider using pagination for large datasets.
     * 
     * @return List of available transfer services
     */
    public List<TransferServiceDTO> getAllAvailableTransfers() {
        log.debug("Fetching all available transfer services");
        return transferServiceRepository.findByIsAvailableTrue(Pageable.unpaged())
                .getContent()
                .stream()
                .map(transferServiceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all available transfer services with pagination.
     * 
     * @param pageable Pagination parameters
     * @return Page of transfer services
     */
    public Page<TransferServiceDTO> getAllAvailableTransfers(Pageable pageable) {
        log.debug("Fetching available transfer services with pagination");
        return transferServiceRepository.findByIsAvailableTrue(pageable)
                .map(transferServiceMapper::toDto);
    }

    /**
     * Finds suitable transfers based on pickup location, dropoff location, and passenger capacity.
     * Health Tourism Critical: Patients often travel with companions and heavy luggage.
     * 
     * Business Logic:
     * - Validates input parameters
     * - Filters by exact location match (case-insensitive)
     * - Filters by minimum passenger capacity
     * - Sorts by rating (descending) and price (ascending)
     * 
     * @param pickup Pickup location (e.g., "İstanbul Havalimanı")
     * @param dropoff Dropoff location (e.g., "Memorial Hastanesi")
     * @param passengers Number of passengers (patient + companions)
     * @return List of suitable transfer services
     * @throws IllegalArgumentException if input parameters are invalid
     */
    public List<TransferServiceDTO> findSuitableTransfers(String pickup, String dropoff, Integer passengers) {
        log.debug("Finding suitable transfers from {} to {} for {} passengers", pickup, dropoff, passengers);
        
        // Business logic validation
        if (pickup == null || pickup.trim().isEmpty()) {
            throw new IllegalArgumentException("Alış noktası boş olamaz.");
        }
        if (dropoff == null || dropoff.trim().isEmpty()) {
            throw new IllegalArgumentException("Bırakış noktası boş olamaz.");
        }
        if (passengers == null || passengers <= 0) {
            throw new IllegalArgumentException("Yolcu sayısı pozitif bir değer olmalıdır. Verilen: " + passengers);
        }
        
        // Use repository's optimized query
        List<TransferService> transfers = transferServiceRepository.findSuitableTransfers(
                pickup.trim(), 
                dropoff.trim(), 
                passengers,
                null // vehicleType is optional
        );
        
        if (transfers.isEmpty()) {
            log.info("No suitable transfers found from {} to {} for {} passengers", pickup, dropoff, passengers);
        }
        
        return transfers.stream()
                .map(transferServiceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Finds suitable transfers with vehicle type filter.
     * 
     * @param pickup Pickup location
     * @param dropoff Dropoff location
     * @param passengers Number of passengers
     * @param vehicleType Vehicle type (Sedan, SUV, Van, Minibus, Bus) - optional
     * @return List of suitable transfer services
     */
    public List<TransferServiceDTO> findSuitableTransfers(
            String pickup, String dropoff, Integer passengers, String vehicleType) {
        log.debug("Finding suitable transfers from {} to {} for {} passengers with vehicle type: {}", 
                pickup, dropoff, passengers, vehicleType);
        
        // Validate inputs
        if (pickup == null || pickup.trim().isEmpty()) {
            throw new IllegalArgumentException("Alış noktası boş olamaz.");
        }
        if (dropoff == null || dropoff.trim().isEmpty()) {
            throw new IllegalArgumentException("Bırakış noktası boş olamaz.");
        }
        if (passengers == null || passengers <= 0) {
            throw new IllegalArgumentException("Yolcu sayısı pozitif bir değer olmalıdır.");
        }
        
        List<TransferService> transfers = transferServiceRepository.findSuitableTransfers(
                pickup.trim(),
                dropoff.trim(),
                passengers,
                vehicleType != null ? vehicleType.trim() : null
        );
        
        return transfers.stream()
                .map(transferServiceMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets transfers by service type.
     * 
     * @param serviceType Service type (e.g., "Airport-Hospital", "Airport-Hotel", "Hotel-Hospital")
     * @return List of matching transfer services
     */
    public List<TransferServiceDTO> getTransfersByType(String serviceType) {
        log.debug("Fetching transfers by type: {}", serviceType);
        
        if (serviceType == null || serviceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Servis tipi boş olamaz.");
        }
        
        return transferServiceRepository.findByServiceTypeAndIsAvailableTrue(
                        serviceType.trim(), Pageable.unpaged())
                .getContent()
                .stream()
                .map(transferServiceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets transfers by service type with pagination.
     * 
     * @param serviceType Service type
     * @param pageable Pagination parameters
     * @return Page of transfer services
     */
    public Page<TransferServiceDTO> getTransfersByType(String serviceType, Pageable pageable) {
        log.debug("Fetching transfers by type: {} with pagination", serviceType);
        
        if (serviceType == null || serviceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Servis tipi boş olamaz.");
        }
        
        return transferServiceRepository.findByServiceTypeAndIsAvailableTrue(serviceType.trim(), pageable)
                .map(transferServiceMapper::toDto);
    }
    
    /**
     * Gets transfers by maximum price.
     * Business Logic: Validates maxPrice is non-negative.
     * 
     * @param maxPrice Maximum price
     * @return List of matching transfer services
     * @throws IllegalArgumentException if maxPrice is negative
     */
    public List<TransferServiceDTO> getTransfersByPrice(BigDecimal maxPrice) {
        log.debug("Fetching transfers with max price: {}", maxPrice);
        
        if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maksimum fiyat negatif olamaz. Verilen: " + maxPrice);
        }
        
        // Use price range query (minPrice = 0)
        return transferServiceRepository.findByPriceRange(
                        BigDecimal.ZERO, maxPrice, Pageable.unpaged())
                .getContent()
                .stream()
                .map(transferServiceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets transfers by price range.
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @param pageable Pagination parameters
     * @return Page of transfer services
     */
    public Page<TransferServiceDTO> getTransfersByPriceRange(
            BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Fetching transfers in price range: {} - {}", minPrice, maxPrice);
        
        if (minPrice == null || minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum fiyat negatif olamaz.");
        }
        if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maksimum fiyat negatif olamaz.");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum fiyat maksimum fiyattan büyük olamaz.");
        }
        
        return transferServiceRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(transferServiceMapper::toDto);
    }

    /**
     * Gets a transfer service by ID.
     * 
     * @param id Transfer service ID
     * @return TransferServiceDTO
     * @throws TransferNotFoundException if transfer not found or not available
     */
    public TransferServiceDTO getTransferById(Long id) {
        log.debug("Fetching transfer service by ID: {}", id);
        return transferServiceRepository.findByIdAndIsAvailableTrue(id)
                .map(transferServiceMapper::toDto)
                .orElseThrow(() -> new TransferNotFoundException(id));
    }
    
    /**
     * Gets VIP transfer services (with meet & greet).
     * Health Tourism Critical: Patients arriving from abroad need airport greeting service.
     * The driver holds a sign with the patient's name in a language they understand.
     * 
     * Business Logic:
     * - Filters services with hasMeetAndGreet = true
     * - Sorts by rating (descending) and price (ascending)
     * 
     * @return List of VIP transfer services
     */
    public List<TransferServiceDTO> getVIPTransfers() {
        log.debug("Fetching VIP transfer services (with meet & greet)");
        return transferServiceRepository.findByHasMeetAndGreetTrueAndIsAvailableTrue()
                .stream()
                .sorted(Comparator.comparing(TransferService::getRating).reversed()
                        .thenComparing(TransferService::getPrice))
                .map(transferServiceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets VIP transfer services with pagination.
     * 
     * @param pageable Pagination parameters
     * @return Page of VIP transfer services
     */
    public Page<TransferServiceDTO> getVIPTransfers(Pageable pageable) {
        log.debug("Fetching VIP transfer services with pagination");
        return transferServiceRepository.findByHasMeetAndGreetTrueAndIsAvailableTrueOrderByRatingDesc(pageable)
                .map(transferServiceMapper::toDto);
    }
    
    /**
     * Gets VIP transfer services by pickup location.
     * 
     * @param pickupLocation Pickup location (typically airport)
     * @return List of VIP transfer services
     */
    public List<TransferServiceDTO> getVIPTransfersByPickupLocation(String pickupLocation) {
        log.debug("Fetching VIP transfers for pickup location: {}", pickupLocation);
        
        if (pickupLocation == null || pickupLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Alış noktası boş olamaz.");
        }
        
        return transferServiceRepository.findVIPServicesByPickupLocation(pickupLocation.trim())
                .stream()
                .map(transferServiceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets transfers by passenger capacity.
     * Health Tourism Critical: Post-surgery patients often travel with companions.
     * 
     * @param minPassengers Minimum passenger capacity
     * @param pageable Pagination parameters
     * @return Page of suitable transfer services
     */
    public Page<TransferServiceDTO> getTransfersByCapacity(Integer minPassengers, Pageable pageable) {
        log.debug("Fetching transfers with minimum capacity: {}", minPassengers);
        
        if (minPassengers == null || minPassengers <= 0) {
            throw new IllegalArgumentException("Minimum yolcu kapasitesi pozitif bir değer olmalıdır.");
        }
        
        return transferServiceRepository.findByMinPassengerCapacity(minPassengers, pageable)
                .map(transferServiceMapper::toDto);
    }
    
    /**
     * Gets transfers by location (pickup and dropoff).
     * 
     * @param pickupLocation Pickup location
     * @param dropoffLocation Dropoff location
     * @param pageable Pagination parameters
     * @return Page of matching transfer services
     */
    public Page<TransferServiceDTO> getTransfersByLocation(
            String pickupLocation, String dropoffLocation, Pageable pageable) {
        log.debug("Fetching transfers from {} to {}", pickupLocation, dropoffLocation);
        
        if (pickupLocation == null || pickupLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Alış noktası boş olamaz.");
        }
        if (dropoffLocation == null || dropoffLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Bırakış noktası boş olamaz.");
        }
        
        return transferServiceRepository.findByPickupLocationAndDropoffLocationAndIsAvailableTrue(
                        pickupLocation.trim(), dropoffLocation.trim(), pageable)
                .map(transferServiceMapper::toDto);
    }
    
    /**
     * Gets top-rated transfers in a price range.
     * 
     * @param minRating Minimum rating (e.g., 4.0)
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @param pageable Pagination parameters
     * @return Page of top-rated transfer services
     */
    public Page<TransferServiceDTO> getTopRatedTransfersInPriceRange(
            Double minRating, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Fetching top-rated transfers (rating >= {}) in price range: {} - {}", 
                minRating, minPrice, maxPrice);
        
        if (minRating == null || minRating < 0 || minRating > 5) {
            throw new IllegalArgumentException("Minimum puan 0-5 arasında olmalıdır.");
        }
        if (minPrice == null || minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum fiyat negatif olamaz.");
        }
        if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maksimum fiyat negatif olamaz.");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum fiyat maksimum fiyattan büyük olamaz.");
        }
        
        return transferServiceRepository.findTopRatedInPriceRange(minRating, minPrice, maxPrice, pageable)
                .map(transferServiceMapper::toDto);
    }
    
    /**
     * Gets cheapest transfers with minimum rating requirement.
     * 
     * @param minRating Minimum rating
     * @param pageable Pagination parameters
     * @return Page of cheapest transfer services
     */
    public Page<TransferServiceDTO> getCheapestTransfers(Double minRating, Pageable pageable) {
        log.debug("Fetching cheapest transfers with minimum rating: {}", minRating);
        
        if (minRating == null || minRating < 0 || minRating > 5) {
            throw new IllegalArgumentException("Minimum puan 0-5 arasında olmalıdır.");
        }
        
        return transferServiceRepository.findCheapestServices(minRating, pageable)
                .map(transferServiceMapper::toDto);
    }
    
    /**
     * Gets transfers with multiple features (VIP + WiFi + Luggage Assistance).
     * 
     * @param hasMeetAndGreet Require meet & greet service
     * @param hasWifi Require WiFi
     * @param hasLuggageAssistance Require luggage assistance
     * @param pageable Pagination parameters
     * @return Page of matching transfer services
     */
    public Page<TransferServiceDTO> getTransfersWithFeatures(
            Boolean hasMeetAndGreet, Boolean hasWifi, Boolean hasLuggageAssistance, Pageable pageable) {
        log.debug("Fetching transfers with features - Meet&Greet: {}, WiFi: {}, Luggage: {}", 
                hasMeetAndGreet, hasWifi, hasLuggageAssistance);
        
        return transferServiceRepository.findByMultipleFeatures(
                        hasMeetAndGreet, hasWifi, hasLuggageAssistance, pageable)
                .map(transferServiceMapper::toDto);
    }
    
    /**
     * Gets nearby transfer services within a maximum distance.
     * 
     * @param pickupLocation Pickup location
     * @param maxDistance Maximum distance in kilometers
     * @return List of nearby transfer services
     */
    public List<TransferServiceDTO> getNearbyTransfers(String pickupLocation, Double maxDistance) {
        log.debug("Fetching nearby transfers for {} within {} km", pickupLocation, maxDistance);
        
        if (pickupLocation == null || pickupLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Alış noktası boş olamaz.");
        }
        if (maxDistance == null || maxDistance < 0) {
            throw new IllegalArgumentException("Maksimum mesafe negatif olamaz.");
        }
        
        return transferServiceRepository.findNearbyServices(pickupLocation.trim(), maxDistance)
                .stream()
                .map(transferServiceMapper::toDto)
                .collect(Collectors.toList());
    }
}
