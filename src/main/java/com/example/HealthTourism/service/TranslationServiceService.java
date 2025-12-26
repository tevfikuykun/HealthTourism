package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.TranslationServiceDTO;
import com.example.HealthTourism.entity.TranslationService;
import com.example.HealthTourism.exception.TranslationServiceNotFoundException;
import com.example.HealthTourism.mapper.TranslationServiceMapper;
import com.example.HealthTourism.repository.TranslationServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enterprise-grade TranslationServiceService with:
 * - Constructor Injection for testability
 * - MapStruct for entity-DTO mapping
 * - Transactional management
 * - Language-based filtering with normalization
 * - Certification validation (critical for medical translation)
 * - Advanced search capabilities
 * - Proper exception handling
 * 
 * Health Tourism Critical: Medical translation errors can lead to malpractice risks.
 * Certification validation is mandatory for hospital/operation room translators.
 */
@Slf4j
@Service
@RequiredArgsConstructor // Constructor Injection - Spring best practice
@Transactional(readOnly = true) // Default: read-only for all methods (performance optimization)
public class TranslationServiceService {
    
    private final TranslationServiceRepository translationServiceRepository;
    private final TranslationServiceMapper translationServiceMapper;

    /**
     * Gets all available translators.
     * 
     * @return List of available translators
     */
    public List<TranslationServiceDTO> getAvailableTranslators() {
        log.debug("Fetching all available translators");
        return translationServiceRepository.findByIsAvailableTrue(Pageable.unpaged())
                .getContent()
                .stream()
                .map(translationServiceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all available translators with pagination.
     * 
     * @param pageable Pagination parameters
     * @return Page of available translators
     */
    public Page<TranslationServiceDTO> getAvailableTranslators(Pageable pageable) {
        log.debug("Fetching available translators with pagination");
        return translationServiceRepository.findByIsAvailableTrue(pageable)
                .map(translationServiceMapper::toDto);
    }

    /**
     * Gets translators by language.
     * Business Logic: Language normalization - case-insensitive search with LIKE.
     * Searches for language in comma-separated languages string.
     * 
     * Example: Searching for "English" will find:
     * - "English, German, Turkish"
     * - "German, English"
     * - "english" (case-insensitive)
     * 
     * @param language Language to search for (e.g., "English", "German", "Turkish")
     * @return List of translators who speak the specified language
     * @throws IllegalArgumentException if language is empty
     */
    public List<TranslationServiceDTO> getTranslatorsByLanguage(String language) {
        log.debug("Fetching translators by language: {}", language);
        
        // Business logic validation: Normalize language input
        String normalizedLanguage = normalizeLanguage(language);
        
        return translationServiceRepository.findByLanguage(normalizedLanguage)
                .stream()
                .map(translationServiceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets translators by language with pagination.
     * 
     * @param language Language to search for
     * @param pageable Pagination parameters
     * @return Page of translators
     */
    public Page<TranslationServiceDTO> getTranslatorsByLanguage(String language, Pageable pageable) {
        log.debug("Fetching translators by language: {} with pagination", language);
        
        String normalizedLanguage = normalizeLanguage(language);
        
        return translationServiceRepository.findByLanguage(normalizedLanguage, pageable)
                .map(translationServiceMapper::toDto);
    }
    
    /**
     * Gets translators by language and certification requirement.
     * Health Tourism Critical: Medical translation requires certified translators.
     * 
     * @param language Language to search for
     * @param requireCertified Whether to require certification (true for medical/hospital translation)
     * @return List of matching translators
     */
    public List<TranslationServiceDTO> getTranslatorsByLanguage(String language, Boolean requireCertified) {
        log.debug("Fetching translators by language: {} (certified: {})", language, requireCertified);
        
        String normalizedLanguage = normalizeLanguage(language);
        
        if (requireCertified != null && requireCertified) {
            return translationServiceRepository.findByLanguageAndCertified(normalizedLanguage)
                    .stream()
                    .map(translationServiceMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return getTranslatorsByLanguage(normalizedLanguage);
        }
    }

    /**
     * Gets hospital translators (available for hospital translation).
     * Health Tourism Critical: Hospital translation is critical for medical procedures.
     * 
     * @return List of hospital translators
     */
    public List<TranslationServiceDTO> getHospitalTranslators() {
        log.debug("Fetching hospital translators");
        return translationServiceRepository.findByIsAvailableForHospitalTrueAndIsAvailableTrue(Pageable.unpaged())
                .getContent()
                .stream()
                .map(translationServiceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets hospital translators with pagination.
     * 
     * @param pageable Pagination parameters
     * @return Page of hospital translators
     */
    public Page<TranslationServiceDTO> getHospitalTranslators(Pageable pageable) {
        log.debug("Fetching hospital translators with pagination");
        return translationServiceRepository.findByIsAvailableForHospitalTrueAndIsAvailableTrue(pageable)
                .map(translationServiceMapper::toDto);
    }
    
    /**
     * Gets certified hospital translators.
     * Health Tourism Critical: Operation room translators MUST be certified.
     * Business Logic: Certification validation for medical safety.
     * 
     * @return List of certified hospital translators
     */
    public List<TranslationServiceDTO> getCertifiedHospitalTranslators() {
        log.debug("Fetching certified hospital translators");
        return translationServiceRepository.findCertifiedHospitalTranslators()
                .stream()
                .map(translationServiceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets certified hospital translators with pagination.
     * 
     * @param pageable Pagination parameters
     * @return Page of certified hospital translators
     */
    public Page<TranslationServiceDTO> getCertifiedHospitalTranslators(Pageable pageable) {
        log.debug("Fetching certified hospital translators with pagination");
        return translationServiceRepository.findCertifiedHospitalTranslators(pageable)
                .map(translationServiceMapper::toDto);
    }

    /**
     * Gets translators by ID.
     * 
     * @param id Translator ID
     * @return TranslationServiceDTO
     * @throws TranslationServiceNotFoundException if translator not found
     */
    public TranslationServiceDTO getTranslatorById(Long id) {
        log.debug("Fetching translator by ID: {}", id);
        return translationServiceRepository.findByIdAndIsAvailableTrue(id)
                .map(translationServiceMapper::toDto)
                .orElseThrow(() -> new TranslationServiceNotFoundException(id));
    }
    
    /**
     * Gets translators by service type.
     * 
     * @param serviceType Service type (Medical, Legal, General, Document)
     * @return List of translators for the service type
     */
    public List<TranslationServiceDTO> getTranslatorsByServiceType(String serviceType) {
        log.debug("Fetching translators by service type: {}", serviceType);
        
        if (serviceType == null || serviceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Hizmet tipi boş olamaz.");
        }
        
        return translationServiceRepository.findByServiceTypeAndIsAvailableTrue(
                        serviceType.trim(), Pageable.unpaged())
                .getContent()
                .stream()
                .map(translationServiceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets translators by service type with pagination.
     * 
     * @param serviceType Service type
     * @param pageable Pagination parameters
     * @return Page of translators
     */
    public Page<TranslationServiceDTO> getTranslatorsByServiceType(String serviceType, Pageable pageable) {
        log.debug("Fetching translators by service type: {} with pagination", serviceType);
        
        if (serviceType == null || serviceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Hizmet tipi boş olamaz.");
        }
        
        return translationServiceRepository.findByServiceTypeAndIsAvailableTrue(serviceType.trim(), pageable)
                .map(translationServiceMapper::toDto);
    }
    
    /**
     * Gets certified translators.
     * 
     * @param pageable Pagination parameters
     * @return Page of certified translators
     */
    public Page<TranslationServiceDTO> getCertifiedTranslators(Pageable pageable) {
        log.debug("Fetching certified translators");
        return translationServiceRepository.findByIsCertifiedTrueAndIsAvailableTrue(pageable)
                .map(translationServiceMapper::toDto);
    }
    
    /**
     * Gets consultation translators (available for consultation translation).
     * 
     * @param pageable Pagination parameters
     * @return Page of consultation translators
     */
    public Page<TranslationServiceDTO> getConsultationTranslators(Pageable pageable) {
        log.debug("Fetching consultation translators");
        return translationServiceRepository.findByIsAvailableForConsultationTrueAndIsAvailableTrue(pageable)
                .map(translationServiceMapper::toDto);
    }
    
    /**
     * Gets certified consultation translators.
     * 
     * @return List of certified consultation translators
     */
    public List<TranslationServiceDTO> getCertifiedConsultationTranslators() {
        log.debug("Fetching certified consultation translators");
        return translationServiceRepository.findCertifiedConsultationTranslators()
                .stream()
                .map(translationServiceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets top-rated medical translators (certified).
     * Health Tourism Critical: Medical translation quality is critical.
     * 
     * @return List of top-rated medical translators
     */
    public List<TranslationServiceDTO> getTopRatedMedicalTranslators() {
        log.debug("Fetching top-rated medical translators");
        return translationServiceRepository.findTopRatedMedicalTranslators()
                .stream()
                .map(translationServiceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets translators by price limit.
     * 
     * @param maxPrice Maximum price per hour
     * @param pageable Pagination parameters
     * @return Page of translators within price limit
     */
    public Page<TranslationServiceDTO> getTranslatorsByPriceLimit(BigDecimal maxPrice, Pageable pageable) {
        log.debug("Fetching translators with max price: {}", maxPrice);
        
        if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maksimum fiyat negatif olamaz.");
        }
        
        return translationServiceRepository.findByPriceLimit(maxPrice, pageable)
                .map(translationServiceMapper::toDto);
    }
    
    /**
     * Gets translators by price range.
     * 
     * @param minPrice Minimum price per hour
     * @param maxPrice Maximum price per hour
     * @param pageable Pagination parameters
     * @return Page of translators in price range
     */
    public Page<TranslationServiceDTO> getTranslatorsByPriceRange(
            BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Fetching translators in price range: {} - {}", minPrice, maxPrice);
        
        if (minPrice == null || minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum fiyat negatif olamaz.");
        }
        if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maksimum fiyat negatif olamaz.");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum fiyat maksimum fiyattan büyük olamaz.");
        }
        
        return translationServiceRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(translationServiceMapper::toDto);
    }
    
    /**
     * Advanced search: Gets translators by language, certification, and price.
     * Health Tourism Critical: Most common use case - find certified translator for specific language within budget.
     * 
     * @param language Language to search for
     * @param isCertified Whether translator must be certified
     * @param maxPrice Maximum price per hour
     * @param pageable Pagination parameters
     * @return Page of matching translators
     */
    public Page<TranslationServiceDTO> searchTranslators(
            String language, Boolean isCertified, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Searching translators - language: {}, certified: {}, maxPrice: {}", 
                language, isCertified, maxPrice);
        
        String normalizedLanguage = normalizeLanguage(language);
        
        if (isCertified == null) {
            isCertified = false; // Default to non-certified if not specified
        }
        
        if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maksimum fiyat negatif olamaz.");
        }
        
        return translationServiceRepository.findByLanguageCertifiedAndPrice(
                        normalizedLanguage, isCertified, maxPrice, pageable)
                .map(translationServiceMapper::toDto);
    }
    
    /**
     * Validates that a translator is certified for hospital/operation room translation.
     * Health Tourism Critical: Certification is MANDATORY for operation room translators.
     * Medical translation errors can lead to malpractice risks.
     * 
     * Business Rule: isCertified = true for hospital translation is mandatory.
     * 
     * @param translatorId Translator ID
     * @param context Translation context (HOSPITAL, CONSULTATION, GENERAL)
     * @throws IllegalArgumentException if certification is required but translator is not certified
     */
    public void validateCertificationForContext(Long translatorId, String context) {
        log.debug("Validating certification for translator ID: {} in context: {}", translatorId, context);
        
        TranslationService translator = translationServiceRepository.findByIdAndIsAvailableTrue(translatorId)
                .orElseThrow(() -> new TranslationServiceNotFoundException(translatorId));
        
        // Business Logic: Hospital/Operation room translation REQUIRES certification
        if ("HOSPITAL".equalsIgnoreCase(context) || "OPERATION_ROOM".equalsIgnoreCase(context)) {
            if (!translator.getIsCertified()) {
                throw new IllegalArgumentException(
                        String.format("Hastane veya ameliyat odası tercümanlığı için sertifikasyon zorunludur. " +
                                "Tercüman '%s' sertifikalı değil.", translator.getTranslatorName()));
            }
            log.debug("Certification validated for translator ID: {} in hospital context", translatorId);
        }
        
        // Business Logic: Medical consultation translation RECOMMENDS certification
        if ("CONSULTATION".equalsIgnoreCase(context) && !translator.getIsCertified()) {
            log.warn("Medical consultation translation without certification: translator ID: {}", translatorId);
        }
    }
    
    // ==================== Private Helper Methods ====================
    
    /**
     * Normalizes language input for search.
     * Business Logic: Trims whitespace, converts to lowercase for consistent search.
     * 
     * @param language Language string to normalize
     * @return Normalized language string
     * @throws IllegalArgumentException if language is null or empty
     */
    private String normalizeLanguage(String language) {
        if (language == null || language.trim().isEmpty()) {
            throw new IllegalArgumentException("Dil adı boş olamaz.");
        }
        
        // Trim and normalize: "  English  " -> "English"
        String normalized = language.trim();
        
        // Optional: Further normalization (e.g., capitalize first letter)
        // normalized = normalized.substring(0, 1).toUpperCase() + normalized.substring(1).toLowerCase();
        
        return normalized;
    }
}

