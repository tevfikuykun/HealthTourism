package com.example.HealthTourism.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Hospital DTO with comprehensive information for health tourism.
 * Includes specializations, accreditations, and operational details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String district;
    private String phone;
    private String email;
    private String description;
    private Double latitude;
    private Double longitude;
    private Double airportDistance;
    private Integer airportDistanceMinutes;
    private Double rating;
    private Integer totalReviews;
    
    /**
     * List of medical specializations available at this hospital.
     * Critical for health tourism: patients search by specialization.
     */
    private List<String> specializations;
    
    /**
     * Hospital accreditations (JCI, ISO, etc.).
     * Builds trust in health tourism sector.
     */
    private Set<String> accreditations;
    
    /**
     * Number of active doctors at the hospital.
     * Provides quick insight into hospital capacity.
     */
    private Integer activeDoctorCount;
}

