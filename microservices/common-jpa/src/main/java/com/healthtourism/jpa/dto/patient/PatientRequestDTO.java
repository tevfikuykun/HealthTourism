package com.healthtourism.jpa.dto.patient;

import com.healthtourism.jpa.dto.BaseRequestDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Patient Request DTO
 * 
 * Used for create and update operations.
 * Does not include sensitive fields or audit fields (ID, createdAt, etc.).
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientRequestDTO extends BaseRequestDTO {
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @NotBlank(message = "Phone is required")
    @Size(min = 10, max = 15, message = "Phone must be between 10 and 15 characters")
    private String phone;
    
    /**
     * Date of birth (optional for update operations)
     */
    private LocalDate dateOfBirth;
    
    /**
     * National ID (TC No) - Optional, should be encrypted
     */
    private String nationalId;
    
    /**
     * Passport number - Optional, should be encrypted
     */
    private String passportNumber;
    
    /**
     * Gender: MALE, FEMALE, OTHER
     */
    private String gender;
    
    /**
     * Blood type (A+, B+, O+, AB+, etc.)
     */
    private String bloodType;
    
    /**
     * Medical history (optional)
     */
    private String medicalHistory;
    
    /**
     * Allergies (optional)
     */
    private String allergies;
}

