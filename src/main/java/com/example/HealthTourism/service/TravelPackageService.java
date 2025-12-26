package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.TravelPackageDTO;
import com.example.HealthTourism.entity.Accommodation;
import com.example.HealthTourism.entity.Doctor;
import com.example.HealthTourism.entity.Hospital;
import com.example.HealthTourism.entity.TravelPackage;
import com.example.HealthTourism.exception.TravelPackageNotFoundException;
import com.example.HealthTourism.mapper.TravelPackageMapper;
import com.example.HealthTourism.repository.AccommodationRepository;
import com.example.HealthTourism.repository.DoctorRepository;
import com.example.HealthTourism.repository.HospitalRepository;
import com.example.HealthTourism.repository.TravelPackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enterprise-grade TravelPackageService with:
 * - Constructor Injection for testability
 * - MapStruct for entity-DTO mapping
 * - Transactional management
 * - Dynamic price calculation (finalPrice = totalPrice - discount)
 * - N+1 query prevention (JOIN FETCH usage)
 * - Comprehensive validations
 * - Package content dynamism (add/remove services)
 * - Proper exception handling
 */
@Slf4j
@Service
@RequiredArgsConstructor // Constructor Injection - Spring best practice
@Transactional(readOnly = true) // Default: read-only for all methods (performance optimization)
public class TravelPackageService {
    
    private final TravelPackageRepository travelPackageRepository;
    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    private final AccommodationRepository accommodationRepository;
    private final TravelPackageMapper travelPackageMapper;
    
    // Business constants
    private static final BigDecimal MAX_DISCOUNT_PERCENTAGE = BigDecimal.valueOf(100);
    private static final BigDecimal MIN_DISCOUNT_PERCENTAGE = BigDecimal.ZERO;

    /**
     * Gets all active travel packages.
     * Performance: Uses JOIN FETCH to prevent N+1 queries.
     * 
     * @return List of active packages with all details
     */
    public List<TravelPackageDTO> getAllActivePackages() {
        log.debug("Fetching all active travel packages");
        return travelPackageRepository.findAllActiveWithDetails()
                .stream()
                .map(travelPackageMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all active travel packages with pagination.
     * 
     * @param pageable Pagination parameters
     * @return Page of active packages
     */
    public Page<TravelPackageDTO> getAllActivePackages(Pageable pageable) {
        log.debug("Fetching active travel packages with pagination");
        return travelPackageRepository.findByIsActiveTrue(pageable)
                .map(travelPackageMapper::toDto);
    }
    
    /**
     * Gets packages by hospital.
     * Performance: Uses JOIN FETCH to prevent N+1 queries.
     * 
     * @param hospitalId Hospital ID
     * @return List of packages for the hospital
     */
    public List<TravelPackageDTO> getPackagesByHospital(Long hospitalId) {
        log.debug("Fetching packages for hospital ID: {}", hospitalId);
        return travelPackageRepository.findByHospitalIdWithDetails(hospitalId)
                .stream()
                .map(travelPackageMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets packages by hospital with pagination.
     * 
     * @param hospitalId Hospital ID
     * @param pageable Pagination parameters
     * @return Page of packages
     */
    public Page<TravelPackageDTO> getPackagesByHospital(Long hospitalId, Pageable pageable) {
        log.debug("Fetching packages for hospital ID: {} with pagination", hospitalId);
        return travelPackageRepository.findByHospitalIdAndIsActiveTrue(hospitalId, pageable)
                .map(travelPackageMapper::toDto);
    }
    
    /**
     * Gets packages by type.
     * 
     * @param packageType Package type (Basic, Standard, Premium, VIP)
     * @return List of packages of the specified type
     */
    public List<TravelPackageDTO> getPackagesByType(String packageType) {
        log.debug("Fetching packages by type: {}", packageType);
        
        if (packageType == null || packageType.trim().isEmpty()) {
            throw new IllegalArgumentException("Paket tipi boş olamaz.");
        }
        
        return travelPackageRepository.findByPackageTypeAndIsActiveTrue(
                        packageType.trim(), Pageable.unpaged())
                .getContent()
                .stream()
                .map(travelPackageMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets packages by type with pagination.
     * 
     * @param packageType Package type
     * @param pageable Pagination parameters
     * @return Page of packages
     */
    public Page<TravelPackageDTO> getPackagesByType(String packageType, Pageable pageable) {
        log.debug("Fetching packages by type: {} with pagination", packageType);
        
        if (packageType == null || packageType.trim().isEmpty()) {
            throw new IllegalArgumentException("Paket tipi boş olamaz.");
        }
        
        return travelPackageRepository.findByPackageTypeAndIsActiveTrue(packageType.trim(), pageable)
                .map(travelPackageMapper::toDto);
    }
    
    /**
     * Gets packages by maximum budget.
     * Business Logic: Filters packages where finalPrice <= maxBudget.
     * 
     * @param maxBudget Maximum budget
     * @return List of packages within budget
     * @throws IllegalArgumentException if maxBudget is negative
     */
    public List<TravelPackageDTO> getPackagesByBudget(BigDecimal maxBudget) {
        log.debug("Fetching packages with max budget: {}", maxBudget);
        
        if (maxBudget == null || maxBudget.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Bütçe negatif olamaz. Verilen: " + maxBudget);
        }
        
        // Use price range query (minPrice = 0)
        return travelPackageRepository.findByPriceRange(
                        BigDecimal.ZERO, maxBudget, Pageable.unpaged())
                .getContent()
                .stream()
                .map(travelPackageMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets packages by price range.
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @param pageable Pagination parameters
     * @return Page of packages in price range
     */
    public Page<TravelPackageDTO> getPackagesByPriceRange(
            BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Fetching packages in price range: {} - {}", minPrice, maxPrice);
        
        if (minPrice == null || minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum fiyat negatif olamaz.");
        }
        if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maksimum fiyat negatif olamaz.");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum fiyat maksimum fiyattan büyük olamaz.");
        }
        
        return travelPackageRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(travelPackageMapper::toDto);
    }
    
    /**
     * Gets a travel package by ID.
     * Performance: Uses JOIN FETCH to prevent N+1 queries.
     * 
     * @param id Package ID
     * @return TravelPackageDTO
     * @throws TravelPackageNotFoundException if package not found
     */
    public TravelPackageDTO getPackageById(Long id) {
        log.debug("Fetching package by ID: {}", id);
        return travelPackageRepository.findByIdWithDetails(id)
                .map(travelPackageMapper::toDto)
                .orElseThrow(() -> new TravelPackageNotFoundException(id));
    }

    /**
     * Creates a new travel package.
     * Business Logic:
     * - Validates all entities exist
     * - Calculates finalPrice automatically (totalPrice - discount)
     * - Sets default values (isActive, rating, totalReviews)
     * 
     * @param travelPackage Travel package entity
     * @return Created TravelPackageDTO
     */
    @Transactional // Override read-only for write operation
    public TravelPackageDTO createPackage(TravelPackage travelPackage) {
        log.info("Creating travel package: {}", travelPackage.getPackageName());
        
        // 1. Validate entities exist
        validatePackageEntities(travelPackage);
        
        // 2. Business Logic: Calculate final price automatically
        calculateFinalPrice(travelPackage);
        
        // 3. Set default values
        travelPackage.setIsActive(true);
        travelPackage.setRating(0.0);
        travelPackage.setTotalReviews(0);
        
        TravelPackage saved = travelPackageRepository.save(travelPackage);
        log.info("Successfully created package with ID: {}", saved.getId());
        
        return travelPackageMapper.toDto(saved);
    }
    
    /**
     * Updates an existing travel package.
     * Business Logic: Recalculates finalPrice if price or discount changed.
     * 
     * @param id Package ID
     * @param travelPackage Updated package data
     * @return Updated TravelPackageDTO
     * @throws TravelPackageNotFoundException if package not found
     */
    @Transactional
    public TravelPackageDTO updatePackage(Long id, TravelPackage travelPackage) {
        log.info("Updating package ID: {}", id);
        
        TravelPackage existing = travelPackageRepository.findById(id)
                .orElseThrow(() -> new TravelPackageNotFoundException(id));
        
        // Validate entities if changed
        if (travelPackage.getHospital() != null) {
            validatePackageEntities(travelPackage);
        }
        
        // Update fields
        if (travelPackage.getPackageName() != null) {
            existing.setPackageName(travelPackage.getPackageName());
        }
        if (travelPackage.getPackageType() != null) {
            existing.setPackageType(travelPackage.getPackageType());
        }
        if (travelPackage.getDurationDays() != null) {
            existing.setDurationDays(travelPackage.getDurationDays());
        }
        if (travelPackage.getTotalPrice() != null) {
            existing.setTotalPrice(travelPackage.getTotalPrice());
        }
        if (travelPackage.getDiscountPercentage() != null) {
            existing.setDiscountPercentage(travelPackage.getDiscountPercentage());
        }
        if (travelPackage.getHospital() != null) {
            existing.setHospital(travelPackage.getHospital());
        }
        if (travelPackage.getDoctor() != null) {
            existing.setDoctor(travelPackage.getDoctor());
        }
        if (travelPackage.getAccommodation() != null) {
            existing.setAccommodation(travelPackage.getAccommodation());
        }
        
        // Update service inclusions
        if (travelPackage.getIncludesFlight() != null) {
            existing.setIncludesFlight(travelPackage.getIncludesFlight());
        }
        if (travelPackage.getIncludesAccommodation() != null) {
            existing.setIncludesAccommodation(travelPackage.getIncludesAccommodation());
        }
        if (travelPackage.getIncludesTransfer() != null) {
            existing.setIncludesTransfer(travelPackage.getIncludesTransfer());
        }
        if (travelPackage.getIncludesCarRental() != null) {
            existing.setIncludesCarRental(travelPackage.getIncludesCarRental());
        }
        if (travelPackage.getIncludesVisaService() != null) {
            existing.setIncludesVisaService(travelPackage.getIncludesVisaService());
        }
        if (travelPackage.getIncludesTranslation() != null) {
            existing.setIncludesTranslation(travelPackage.getIncludesTranslation());
        }
        if (travelPackage.getIncludesInsurance() != null) {
            existing.setIncludesInsurance(travelPackage.getIncludesInsurance());
        }
        
        if (travelPackage.getDescription() != null) {
            existing.setDescription(travelPackage.getDescription());
        }
        
        // Recalculate final price if price or discount changed
        calculateFinalPrice(existing);
        
        TravelPackage saved = travelPackageRepository.save(existing);
        log.info("Successfully updated package ID: {}", id);
        
        return travelPackageMapper.toDto(saved);
    }
    
    /**
     * Adds a service to an existing package.
     * Health Tourism Critical: Allows dynamic package customization.
     * Example: Patient wants to add extra translation hours or city tour.
     * 
     * @param packageId Package ID
     * @param serviceType Service type (FLIGHT, ACCOMMODATION, TRANSFER, CAR_RENTAL, VISA, TRANSLATION, INSURANCE)
     * @return Updated TravelPackageDTO
     */
    @Transactional
    public TravelPackageDTO addServiceToPackage(Long packageId, String serviceType) {
        log.info("Adding service {} to package ID: {}", serviceType, packageId);
        
        TravelPackage travelPackage = travelPackageRepository.findById(packageId)
                .orElseThrow(() -> new TravelPackageNotFoundException(packageId));
        
        switch (serviceType.toUpperCase()) {
            case "FLIGHT":
                travelPackage.setIncludesFlight(true);
                break;
            case "ACCOMMODATION":
                travelPackage.setIncludesAccommodation(true);
                break;
            case "TRANSFER":
                travelPackage.setIncludesTransfer(true);
                break;
            case "CAR_RENTAL":
                travelPackage.setIncludesCarRental(true);
                break;
            case "VISA":
                travelPackage.setIncludesVisaService(true);
                break;
            case "TRANSLATION":
                travelPackage.setIncludesTranslation(true);
                break;
            case "INSURANCE":
                travelPackage.setIncludesInsurance(true);
                break;
            default:
                throw new IllegalArgumentException("Geçersiz servis tipi: " + serviceType);
        }
        
        // Note: In a real scenario, you might want to recalculate price based on added service
        // This would require pricing service integration
        
        TravelPackage saved = travelPackageRepository.save(travelPackage);
        log.info("Successfully added service {} to package ID: {}", serviceType, packageId);
        
        return travelPackageMapper.toDto(saved);
    }
    
    /**
     * Removes a service from an existing package.
     * 
     * @param packageId Package ID
     * @param serviceType Service type to remove
     * @return Updated TravelPackageDTO
     */
    @Transactional
    public TravelPackageDTO removeServiceFromPackage(Long packageId, String serviceType) {
        log.info("Removing service {} from package ID: {}", serviceType, packageId);
        
        TravelPackage travelPackage = travelPackageRepository.findById(packageId)
                .orElseThrow(() -> new TravelPackageNotFoundException(packageId));
        
        switch (serviceType.toUpperCase()) {
            case "FLIGHT":
                travelPackage.setIncludesFlight(false);
                break;
            case "ACCOMMODATION":
                travelPackage.setIncludesAccommodation(false);
                break;
            case "TRANSFER":
                travelPackage.setIncludesTransfer(false);
                break;
            case "CAR_RENTAL":
                travelPackage.setIncludesCarRental(false);
                break;
            case "VISA":
                travelPackage.setIncludesVisaService(false);
                break;
            case "TRANSLATION":
                travelPackage.setIncludesTranslation(false);
                break;
            case "INSURANCE":
                travelPackage.setIncludesInsurance(false);
                break;
            default:
                throw new IllegalArgumentException("Geçersiz servis tipi: " + serviceType);
        }
        
        TravelPackage saved = travelPackageRepository.save(travelPackage);
        log.info("Successfully removed service {} from package ID: {}", serviceType, packageId);
        
        return travelPackageMapper.toDto(saved);
    }
    
    /**
     * Updates package discount percentage and recalculates final price.
     * 
     * @param packageId Package ID
     * @param discountPercentage New discount percentage (0-100)
     * @return Updated TravelPackageDTO
     */
    @Transactional
    public TravelPackageDTO updateDiscount(Long packageId, BigDecimal discountPercentage) {
        log.info("Updating discount for package ID: {} to {}%", packageId, discountPercentage);
        
        // Validate discount percentage
        if (discountPercentage == null || 
            discountPercentage.compareTo(MIN_DISCOUNT_PERCENTAGE) < 0 || 
            discountPercentage.compareTo(MAX_DISCOUNT_PERCENTAGE) > 0) {
            throw new IllegalArgumentException(
                    String.format("İndirim yüzdesi 0-100 arasında olmalıdır. Verilen: %s", discountPercentage));
        }
        
        TravelPackage travelPackage = travelPackageRepository.findById(packageId)
                .orElseThrow(() -> new TravelPackageNotFoundException(packageId));
        
        travelPackage.setDiscountPercentage(discountPercentage);
        calculateFinalPrice(travelPackage);
        
        TravelPackage saved = travelPackageRepository.save(travelPackage);
        log.info("Successfully updated discount for package ID: {}", packageId);
        
        return travelPackageMapper.toDto(saved);
    }
    
    /**
     * Gets VIP packages (Premium or VIP type).
     * 
     * @param pageable Pagination parameters
     * @return Page of VIP packages
     */
    public Page<TravelPackageDTO> getVIPPackages(Pageable pageable) {
        log.debug("Fetching VIP packages");
        return travelPackageRepository.findVIPPackages(pageable)
                .map(travelPackageMapper::toDto);
    }
    
    /**
     * Gets complete packages (includes flight, accommodation, and transfer).
     * 
     * @param pageable Pagination parameters
     * @return Page of complete packages
     */
    public Page<TravelPackageDTO> getCompletePackages(Pageable pageable) {
        log.debug("Fetching complete packages");
        return travelPackageRepository.findCompletePackages(pageable)
                .map(travelPackageMapper::toDto);
    }
    
    /**
     * Searches packages by keyword (package name or hospital name).
     * 
     * @param keyword Search keyword
     * @param pageable Pagination parameters
     * @return Page of matching packages
     */
    public Page<TravelPackageDTO> searchPackages(String keyword, Pageable pageable) {
        log.debug("Searching packages with keyword: {}", keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Arama kelimesi boş olamaz.");
        }
        
        return travelPackageRepository.searchByKeyword(keyword.trim(), pageable)
                .map(travelPackageMapper::toDto);
    }
    
    // ==================== Private Helper Methods ====================
    
    /**
     * Calculates final price based on totalPrice and discountPercentage.
     * Formula: finalPrice = totalPrice - (totalPrice × discountPercentage / 100)
     * 
     * Business Logic: Ensures data consistency - finalPrice is always calculated,
     * never manually set.
     * 
     * @param travelPackage Package to calculate price for
     */
    private void calculateFinalPrice(TravelPackage travelPackage) {
        if (travelPackage.getTotalPrice() == null) {
            throw new IllegalArgumentException("Toplam fiyat boş olamaz.");
        }
        
        if (travelPackage.getTotalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Toplam fiyat negatif olamaz.");
        }
        
        BigDecimal discountPercentage = travelPackage.getDiscountPercentage();
        if (discountPercentage == null) {
            discountPercentage = BigDecimal.ZERO;
        }
        
        // Validate discount percentage
        if (discountPercentage.compareTo(MIN_DISCOUNT_PERCENTAGE) < 0 || 
            discountPercentage.compareTo(MAX_DISCOUNT_PERCENTAGE) > 0) {
            throw new IllegalArgumentException(
                    String.format("İndirim yüzdesi 0-100 arasında olmalıdır. Verilen: %s", discountPercentage));
        }
        
        // Calculate discount amount
        BigDecimal discountAmount = travelPackage.getTotalPrice()
                .multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        
        // Calculate final price
        BigDecimal finalPrice = travelPackage.getTotalPrice().subtract(discountAmount);
        
        // Ensure final price is not negative
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
            log.warn("Final price was negative for package {}, setting to 0", travelPackage.getPackageName());
        }
        
        travelPackage.setFinalPrice(finalPrice);
        log.debug("Calculated final price for package {}: {} (total: {}, discount: {}%)", 
                travelPackage.getPackageName(), finalPrice, travelPackage.getTotalPrice(), discountPercentage);
    }
    
    /**
     * Validates that all entities referenced in the package exist.
     * 
     * @param travelPackage Package to validate
     */
    private void validatePackageEntities(TravelPackage travelPackage) {
        // Validate hospital
        if (travelPackage.getHospital() != null && travelPackage.getHospital().getId() != null) {
            Hospital hospital = hospitalRepository.findByIdAndIsActiveTrue(
                    travelPackage.getHospital().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Hastane bulunamadı veya aktif değil: " + travelPackage.getHospital().getId()));
            travelPackage.setHospital(hospital);
        }
        
        // Validate doctor (optional)
        if (travelPackage.getDoctor() != null && travelPackage.getDoctor().getId() != null) {
            Doctor doctor = doctorRepository.findByIdAndIsAvailableTrue(
                    travelPackage.getDoctor().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Doktor bulunamadı veya müsait değil: " + travelPackage.getDoctor().getId()));
            
            // Business logic: Doctor must work at the specified hospital
            if (travelPackage.getHospital() != null && 
                !doctor.getHospital().getId().equals(travelPackage.getHospital().getId())) {
                throw new IllegalArgumentException(
                        String.format("Doktor %s bu hastanede çalışmıyor.", doctor.getFirstName()));
            }
            
            travelPackage.setDoctor(doctor);
        }
        
        // Validate accommodation (optional)
        if (travelPackage.getAccommodation() != null && travelPackage.getAccommodation().getId() != null) {
            Accommodation accommodation = accommodationRepository.findByIdAndIsActiveTrue(
                    travelPackage.getAccommodation().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Konaklama bulunamadı veya aktif değil: " + travelPackage.getAccommodation().getId()));
            
            // Business logic: Accommodation should be near the hospital (optional check)
            if (travelPackage.getHospital() != null && 
                !accommodation.getHospital().getId().equals(travelPackage.getHospital().getId())) {
                log.warn("Accommodation {} is not associated with hospital {}", 
                        accommodation.getName(), travelPackage.getHospital().getName());
            }
            
            travelPackage.setAccommodation(accommodation);
        }
    }
}
