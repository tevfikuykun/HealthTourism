package com.healthtourism.jpa.dto.patient;

import com.healthtourism.jpa.dto.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Patient Response DTO
 * 
 * Used for read operations.
 * Includes ID, timestamps, and audit fields.
 * Sensitive fields (nationalId, passportNumber) are typically excluded or masked.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientResponseDTO extends BaseResponseDTO {
    
    private UUID id;
    
    private String firstName;
    
    private String lastName;
    
    /**
     * Full name (computed: firstName + lastName)
     */
    private String fullName;
    
    private String email;
    
    private String phone;
    
    private LocalDate dateOfBirth;
    
    /**
     * Age (computed from dateOfBirth)
     */
    private Integer age;
    
    /**
     * National ID - Typically excluded or masked in response
     * Only included if user has proper permissions
     */
    private String nationalId; // TODO: Mask or exclude for security
    
    /**
     * Passport Number - Typically excluded or masked in response
     * Only included if user has proper permissions
     */
    private String passportNumber; // TODO: Mask or exclude for security
    
    /**
     * Gender: MALE, FEMALE, OTHER
     */
    private String gender;
    
    /**
     * Blood type
     */
    private String bloodType;
    
    /**
     * Medical history
     */
    private String medicalHistory;
    
    /**
     * Allergies
     */
    private String allergies;
    
    /**
     * Created at timestamp
     */
    private java.time.LocalDateTime createdAt;
    
    /**
     * Updated at timestamp
     */
    private java.time.LocalDateTime updatedAt;
    
    /**
     * Created by user identifier
     */
    private String createdBy;
    
    /**
     * Updated by user identifier
     */
    private String updatedBy;
}

