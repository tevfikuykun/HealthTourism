package com.healthtourism.doctorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Hospital Summary DTO
 * 
 * Simplified hospital information for nested objects in responses.
 * Used in DoctorResponseDTO to avoid full hospital entity exposure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalSummaryDTO {
    
    private Long id;
    private String name;
    private String city;
    private String country;
    private String address;
    
    // Optional fields
    private String phone;
    private String email;
    private Double rating;
    private String imageUrl;
}

