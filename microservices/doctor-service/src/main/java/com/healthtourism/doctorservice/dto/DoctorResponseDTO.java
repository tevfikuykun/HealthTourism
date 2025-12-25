package com.healthtourism.doctorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Doctor Response DTO
 * 
 * Professional response DTO for displaying doctor information.
 * Contains all fields that should be visible to API consumers.
 * 
 * Features:
 * - Builder pattern for easy object construction
 * - List-based fields for flexibility (specializations, languages)
 * - Currency support for international payments
 * - Nested HospitalSummaryDTO for related data
 * - Computed fullName field
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponseDTO {
    
    private Long id;
    
    private String title; // "Prof. Dr.", "Doç. Dr.", "Dr." etc.
    private String firstName;
    private String lastName;
    
    /**
     * Full name computed as: title + firstName + lastName
     * Example: "Prof. Dr. Ahmet Yılmaz"
     */
    private String fullName;
    
    /**
     * List of specializations for flexibility
     * Example: ["Kardiyoloji", "Kalp Damar Cerrahisi"]
     */
    private List<String> specializations;
    
    private String bio;
    private Integer experienceYears;
    
    /**
     * List of languages spoken by the doctor
     * Example: ["Türkçe", "English", "Deutsch"]
     */
    private List<String> languages;
    
    /**
     * System-managed fields (calculated/aggregated)
     */
    private Double rating;
    private Integer totalReviews;
    
    /**
     * Consultation fee with currency support
     * Example: currency="EUR", consultationFee=500.0
     */
    private String currency; // EUR, USD, TRY, GBP, etc.
    private Double consultationFee;
    
    private Boolean isAvailable;
    
    /**
     * Nested hospital information (summary)
     * Avoids full entity exposure while providing essential hospital data
     */
    private HospitalSummaryDTO hospital;
    
    /**
     * Image URLs for doctor profile
     */
    private String imageUrl;
    private String thumbnailUrl;
}

