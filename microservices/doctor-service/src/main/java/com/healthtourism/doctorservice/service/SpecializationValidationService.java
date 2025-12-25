package com.healthtourism.doctorservice.service;

import com.healthtourism.doctorservice.exception.InvalidSpecializationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Specialization Validation Service
 * 
 * Validates medical specializations against a predefined list.
 * In production, this could be fetched from a database or external service.
 */
@Service
@RequiredArgsConstructor
public class SpecializationValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(SpecializationValidationService.class);
    
    /**
     * Valid medical specializations
     * In production, this should be stored in database (TreatmentBranch table)
     */
    private static final Set<String> VALID_SPECIALIZATIONS = Set.of(
        "Kardiyoloji",
        "Kalp Damar Cerrahisi",
        "Nöroloji",
        "Nöroşirurji",
        "Ortopedi",
        "Genel Cerrahi",
        "Plastik Cerrahi",
        "Estetik Cerrahi",
        "Göz Hastalıkları",
        "Kulak Burun Boğaz",
        "Dermatoloji",
        "Üroloji",
        "Jinekoloji",
        "Onkoloji",
        "Radyoloji",
        "Anestezi",
        "Fizik Tedavi",
        "Psikiyatri",
        "Çocuk Sağlığı",
        "İç Hastalıkları",
        "Gastroenteroloji",
        "Endokrinoloji",
        "Romatoloji",
        "Hematoloji",
        "Nefroloji",
        "Pulmonoloji",
        "Cardiology",
        "Cardiovascular Surgery",
        "Neurology",
        "Neurosurgery",
        "Orthopedics",
        "General Surgery",
        "Plastic Surgery",
        "Aesthetic Surgery",
        "Ophthalmology",
        "ENT",
        "Dermatology",
        "Urology",
        "Gynecology",
        "Oncology",
        "Radiology",
        "Anesthesiology",
        "Physical Therapy",
        "Psychiatry",
        "Pediatrics",
        "Internal Medicine",
        "Gastroenterology",
        "Endocrinology",
        "Rheumatology",
        "Hematology",
        "Nephrology",
        "Pulmonology"
    );
    
    /**
     * Validate a single specialization
     */
    public void validateSpecialization(String specialization) {
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new InvalidSpecializationException("Uzmanlık alanı boş olamaz");
        }
        
        String normalized = normalizationSpecialization(specialization);
        
        if (!VALID_SPECIALIZATIONS.contains(normalized)) {
            logger.warn("Invalid specialization attempted: {}", specialization);
            throw new InvalidSpecializationException(
                String.format("Geçersiz uzmanlık alanı: %s. Geçerli uzmanlık alanları: %s", 
                    specialization, String.join(", ", VALID_SPECIALIZATIONS))
            );
        }
    }
    
    /**
     * Validate multiple specializations
     */
    public void validateSpecializations(List<String> specializations) {
        if (specializations == null || specializations.isEmpty()) {
            throw new InvalidSpecializationException("En az bir uzmanlık alanı belirtilmelidir");
        }
        
        for (String specialization : specializations) {
            validateSpecialization(specialization);
        }
    }
    
    /**
     * Normalize specialization name (case-insensitive, trim)
     */
    private String normalizationSpecialization(String specialization) {
        return specialization.trim();
    }
    
    /**
     * Get all valid specializations
     */
    public Set<String> getValidSpecializations() {
        return VALID_SPECIALIZATIONS;
    }
}

