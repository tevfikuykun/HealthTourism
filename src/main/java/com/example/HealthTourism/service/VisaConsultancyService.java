package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.VisaConsultancyDTO;
import com.example.HealthTourism.entity.VisaConsultancy;
import com.example.HealthTourism.exception.VisaConsultancyNotFoundException;
import com.example.HealthTourism.mapper.VisaConsultancyMapper;
import com.example.HealthTourism.repository.VisaConsultancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enterprise-grade VisaConsultancyService with:
 * - Constructor Injection for testability
 * - MapStruct for entity-DTO mapping
 * - Transactional management
 * - Success rate validation (Premium packages require >= 95%)
 * - Required documents parsing (String to List)
 * - Fastest consultants prioritization
 * - Advanced search capabilities
 * - Proper exception handling
 * 
 * Health Tourism Critical: Medical visa is different from tourist visa.
 * Success rate validation ensures only reliable consultants are shown in Premium packages.
 */
@Slf4j
@Service
@RequiredArgsConstructor // Constructor Injection - Spring best practice
@Transactional(readOnly = true) // Default: read-only for all methods (performance optimization)
public class VisaConsultancyService {
    
    private final VisaConsultancyRepository visaConsultancyRepository;
    private final VisaConsultancyMapper visaConsultancyMapper;
    
    // Business constants
    private static final Double PREMIUM_SUCCESS_RATE_THRESHOLD = 95.0;
    private static final String DEFAULT_REQUIRED_DOCUMENTS_SEPARATOR = ",";

    /**
     * Gets consultants by country and visa type.
     * 
     * @param country Target country (e.g., "Turkey")
     * @param visaType Visa type (Medical, Tourist, Business)
     * @return List of matching consultants
     */
    public List<VisaConsultancyDTO> getConsultantsByCountryAndType(String country, String visaType) {
        log.debug("Fetching consultants for country: {} and visa type: {}", country, visaType);
        
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Ülke adı boş olamaz.");
        }
        if (visaType == null || visaType.trim().isEmpty()) {
            throw new IllegalArgumentException("Vize tipi boş olamaz.");
        }
        
        return visaConsultancyRepository.findByCountryAndVisaType(
                        country.trim(), visaType.trim(), Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets consultants by country and visa type with pagination.
     * 
     * @param country Target country
     * @param visaType Visa type
     * @param pageable Pagination parameters
     * @return Page of matching consultants
     */
    public Page<VisaConsultancyDTO> getConsultantsByCountryAndType(
            String country, String visaType, Pageable pageable) {
        log.debug("Fetching consultants for country: {} and visa type: {} with pagination", country, visaType);
        
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Ülke adı boş olamaz.");
        }
        if (visaType == null || visaType.trim().isEmpty()) {
            throw new IllegalArgumentException("Vize tipi boş olamaz.");
        }
        
        return visaConsultancyRepository.findByCountryAndVisaType(
                        country.trim(), visaType.trim(), pageable)
                .map(this::enrichDTO);
    }

    /**
     * Gets fastest consultants for a country.
     * Health Tourism Critical: When surgery date is approaching, finding fastest consultant is critical.
     * 
     * Business Logic: Sorts by averageProcessingDays (ascending), then by processingDays, then by successRate (descending).
     * 
     * @param country Target country
     * @return List of consultants sorted by processing time (fastest first)
     */
    public List<VisaConsultancyDTO> getFastestConsultants(String country) {
        log.debug("Fetching fastest consultants for country: {}", country);
        
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Ülke adı boş olamaz.");
        }
        
        return visaConsultancyRepository.findBySupportedCountry(country.trim())
                .stream()
                .sorted(Comparator
                        .comparing(VisaConsultancy::getAverageProcessingDays, 
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(VisaConsultancy::getProcessingDays)
                        .thenComparing(VisaConsultancy::getSuccessRate, Comparator.<Double>reverseOrder()))
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets fastest consultants for country and visa type.
     * 
     * @param country Target country
     * @param visaType Visa type
     * @return List of fastest consultants
     */
    public List<VisaConsultancyDTO> getFastestConsultants(String country, String visaType) {
        log.debug("Fetching fastest consultants for country: {} and visa type: {}", country, visaType);
        
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Ülke adı boş olamaz.");
        }
        if (visaType == null || visaType.trim().isEmpty()) {
            throw new IllegalArgumentException("Vize tipi boş olamaz.");
        }
        
        return visaConsultancyRepository.findFastestConsultants(country.trim(), visaType.trim())
                .stream()
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets a consultant by ID.
     * 
     * @param id Consultant ID
     * @return VisaConsultancyDTO
     * @throws VisaConsultancyNotFoundException if consultant not found
     */
    public VisaConsultancyDTO getConsultancyById(Long id) {
        log.debug("Fetching consultant by ID: {}", id);
        return visaConsultancyRepository.findByIdAndIsAvailableTrue(id)
                .map(this::enrichDTO)
                .orElseThrow(() -> new VisaConsultancyNotFoundException(id));
    }
    
    /**
     * Gets all available consultants.
     * 
     * @param pageable Pagination parameters
     * @return Page of available consultants
     */
    public Page<VisaConsultancyDTO> getAvailableConsultants(Pageable pageable) {
        log.debug("Fetching available consultants with pagination");
        return visaConsultancyRepository.findByIsAvailableTrue(pageable)
                .map(this::enrichDTO);
    }
    
    /**
     * Gets consultants by country.
     * 
     * @param country Target country
     * @param pageable Pagination parameters
     * @return Page of consultants for the country
     */
    public Page<VisaConsultancyDTO> getConsultantsByCountry(String country, Pageable pageable) {
        log.debug("Fetching consultants for country: {} with pagination", country);
        
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Ülke adı boş olamaz.");
        }
        
        return visaConsultancyRepository.findByCountryAndIsAvailableTrue(country.trim(), pageable)
                .map(this::enrichDTO);
    }
    
    /**
     * Gets consultants by visa type.
     * 
     * @param visaType Visa type (Medical, Tourist, Business)
     * @param pageable Pagination parameters
     * @return Page of consultants for the visa type
     */
    public Page<VisaConsultancyDTO> getConsultantsByVisaType(String visaType, Pageable pageable) {
        log.debug("Fetching consultants for visa type: {} with pagination", visaType);
        
        if (visaType == null || visaType.trim().isEmpty()) {
            throw new IllegalArgumentException("Vize tipi boş olamaz.");
        }
        
        return visaConsultancyRepository.findByVisaTypeAndIsAvailableTrue(visaType.trim(), pageable)
                .map(this::enrichDTO);
    }
    
    /**
     * Gets premium consultants (success rate >= 95%).
     * Business Rule: Premium packages only show consultants with successRate >= 95%.
     * Health Tourism Critical: Medical visa requires high reliability.
     * 
     * @param pageable Pagination parameters
     * @return Page of premium consultants
     */
    public Page<VisaConsultancyDTO> getPremiumConsultants(Pageable pageable) {
        log.debug("Fetching premium consultants (success rate >= {}%)", PREMIUM_SUCCESS_RATE_THRESHOLD);
        return visaConsultancyRepository.findByMinSuccessRate(PREMIUM_SUCCESS_RATE_THRESHOLD, pageable)
                .map(this::enrichDTO);
    }
    
    /**
     * Gets premium consultants within price limit.
     * 
     * @param maxFee Maximum consultancy fee
     * @param pageable Pagination parameters
     * @return Page of premium consultants within price limit
     */
    public Page<VisaConsultancyDTO> getPremiumConsultants(BigDecimal maxFee, Pageable pageable) {
        log.debug("Fetching premium consultants with max fee: {}", maxFee);
        
        if (maxFee == null || maxFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maksimum ücret negatif olamaz.");
        }
        
        return visaConsultancyRepository.findPremiumConsultants(maxFee, pageable)
                .map(this::enrichDTO);
    }
    
    /**
     * Gets emergency visa consultants.
     * Health Tourism Critical: Emergency visa support for critical cases.
     * 
     * @return List of emergency visa consultants
     */
    public List<VisaConsultancyDTO> getEmergencyConsultants() {
        log.debug("Fetching emergency visa consultants");
        return visaConsultancyRepository.findEmergencyVisaConsultants()
                .stream()
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets emergency consultants for country and visa type.
     * 
     * @param country Target country
     * @param visaType Visa type
     * @return List of emergency consultants
     */
    public List<VisaConsultancyDTO> getEmergencyConsultants(String country, String visaType) {
        log.debug("Fetching emergency consultants for country: {} and visa type: {}", country, visaType);
        
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Ülke adı boş olamaz.");
        }
        if (visaType == null || visaType.trim().isEmpty()) {
            throw new IllegalArgumentException("Vize tipi boş olamaz.");
        }
        
        return visaConsultancyRepository.findEmergencyConsultantsByCountryAndType(
                        country.trim(), visaType.trim())
                .stream()
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets consultants by maximum processing days.
     * Health Tourism Critical: When surgery date is approaching, need consultants who can process within deadline.
     * 
     * @param maxDays Maximum processing days
     * @return List of consultants who can process within maxDays
     */
    public List<VisaConsultancyDTO> getConsultantsByMaxProcessingDays(Integer maxDays) {
        log.debug("Fetching consultants with max processing days: {}", maxDays);
        
        if (maxDays == null || maxDays <= 0) {
            throw new IllegalArgumentException("Maksimum işlem günü pozitif bir değer olmalıdır.");
        }
        
        return visaConsultancyRepository.findByMaxProcessingDays(maxDays)
                .stream()
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets fastest consultants with minimum success rate.
     * 
     * @param minSuccessRate Minimum success rate (0-100)
     * @param pageable Pagination parameters
     * @return Page of fastest consultants with min success rate
     */
    public Page<VisaConsultancyDTO> getFastestConsultantsWithMinSuccessRate(
            Double minSuccessRate, Pageable pageable) {
        log.debug("Fetching fastest consultants with min success rate: {}%", minSuccessRate);
        
        if (minSuccessRate == null || minSuccessRate < 0 || minSuccessRate > 100) {
            throw new IllegalArgumentException("Başarı oranı 0-100 arasında olmalıdır.");
        }
        
        return visaConsultancyRepository.findFastestWithMinSuccessRate(minSuccessRate, pageable)
                .map(this::enrichDTO);
    }
    
    /**
     * Advanced search with multiple criteria.
     * Health Tourism Critical: Most common use case - find consultant with specific requirements.
     * 
     * @param country Target country (optional)
     * @param visaType Visa type (optional)
     * @param maxProcessingDays Maximum processing days (optional)
     * @param minSuccessRate Minimum success rate (optional)
     * @param maxFee Maximum consultancy fee (optional)
     * @param pageable Pagination parameters
     * @return Page of matching consultants
     */
    public Page<VisaConsultancyDTO> searchConsultants(
            String country, String visaType, Integer maxProcessingDays, 
            Double minSuccessRate, BigDecimal maxFee, Pageable pageable) {
        log.debug("Advanced search - country: {}, visaType: {}, maxDays: {}, minSuccess: {}%, maxFee: {}", 
                country, visaType, maxProcessingDays, minSuccessRate, maxFee);
        
        // Validate success rate if provided
        if (minSuccessRate != null && (minSuccessRate < 0 || minSuccessRate > 100)) {
            throw new IllegalArgumentException("Başarı oranı 0-100 arasında olmalıdır.");
        }
        
        // Validate fee if provided
        if (maxFee != null && maxFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maksimum ücret negatif olamaz.");
        }
        
        return visaConsultancyRepository.searchAdvanced(
                        country != null ? country.trim() : null,
                        visaType != null ? visaType.trim() : null,
                        maxProcessingDays,
                        minSuccessRate,
                        maxFee,
                        pageable)
                .map(this::enrichDTO);
    }
    
    /**
     * Validates that consultant meets premium package requirements.
     * Business Rule: Premium packages require successRate >= 95%.
     * 
     * @param consultantId Consultant ID
     * @param packageType Package type (PREMIUM requires >= 95% success rate)
     * @throws IllegalArgumentException if consultant doesn't meet premium requirements
     */
    public void validatePremiumRequirement(Long consultantId, String packageType) {
        log.debug("Validating premium requirement for consultant ID: {} in package: {}", consultantId, packageType);
        
        if ("PREMIUM".equalsIgnoreCase(packageType) || "VIP".equalsIgnoreCase(packageType)) {
            VisaConsultancy consultant = visaConsultancyRepository.findByIdAndIsAvailableTrue(consultantId)
                    .orElseThrow(() -> new VisaConsultancyNotFoundException(consultantId));
            
            if (consultant.getSuccessRate() == null || 
                consultant.getSuccessRate() < PREMIUM_SUCCESS_RATE_THRESHOLD) {
                throw new IllegalArgumentException(
                        String.format("Premium paketler için başarı oranı en az %%%.0f olmalıdır. " +
                                "Danışman '%s' için başarı oranı: %%%.1f", 
                                PREMIUM_SUCCESS_RATE_THRESHOLD,
                                consultant.getCompanyName(),
                                consultant.getSuccessRate() != null ? consultant.getSuccessRate() : 0.0));
            }
            
            log.debug("Premium requirement validated for consultant ID: {}", consultantId);
        }
    }
    
    // ==================== Private Helper Methods ====================
    
    /**
     * Enriches DTO with parsed required documents list.
     * Business Logic: Parses comma-separated requiredDocuments string into List<String>.
     * This improves UX on frontend - documents can be displayed as a list.
     * 
     * @param entity VisaConsultancy entity
     * @return Enriched VisaConsultancyDTO with parsed requiredDocumentsList
     */
    private VisaConsultancyDTO enrichDTO(VisaConsultancy entity) {
        VisaConsultancyDTO dto = visaConsultancyMapper.toDto(entity);
        
        // Parse requiredDocuments string into list for better UX
        String requiredDocs = dto.getRequiredDocuments();
        if (requiredDocs != null && !requiredDocs.trim().isEmpty()) {
            List<String> docsList = Arrays.stream(requiredDocs.split(DEFAULT_REQUIRED_DOCUMENTS_SEPARATOR))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            dto.setRequiredDocumentsList(docsList);
        } else {
            // Set empty list if no documents specified
            dto.setRequiredDocumentsList(java.util.Collections.emptyList());
        }
        
        return dto;
    }
}

