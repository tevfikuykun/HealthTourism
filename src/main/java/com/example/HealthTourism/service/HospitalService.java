package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.HospitalDTO;
import com.example.HealthTourism.entity.Hospital;
import com.example.HealthTourism.exception.HospitalNotFoundException;
import com.example.HealthTourism.mapper.HospitalMapper;
import com.example.HealthTourism.repository.DoctorRepository;
import com.example.HealthTourism.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Enterprise-grade HospitalService with:
 * - Constructor Injection for testability
 * - MapStruct for entity-DTO mapping
 * - Transactional management
 * - Business logic validation (distance validation, data integrity)
 * - N+1 query prevention (JOIN FETCH usage)
 * - Specialization listing (critical for health tourism)
 * - Proper exception handling
 */
@Slf4j
@Service
@RequiredArgsConstructor // Constructor Injection - Spring best practice
@Transactional(readOnly = true) // Default: read-only for all methods (performance optimization)
public class HospitalService {
    
    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalMapper hospitalMapper;

    /**
     * Gets all active hospitals.
     * Performance: Uses JOIN FETCH to prevent N+1 query problem.
     * Cache: Results are cached for 5 minutes to reduce database load.
     * 
     * @return List of active hospitals with details
     */
    @Cacheable(value = "hospitals", key = "'all-active'")
    public List<HospitalDTO> getAllActiveHospitals() {
        log.debug("Fetching all active hospitals");
        
        // Use deprecated method for backward compatibility
        // Note: For large datasets, consider using pagination version with Pageable
        return hospitalRepository.findAllActiveOrderByRatingDesc()
                .stream()
                .map(this::toDtoWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * Gets a single hospital by ID.
     * Performance: Uses JOIN FETCH to load related entities in single query (prevents N+1).
     * Enriches DTO with specializations and active doctor count.
     * 
     * @param id Hospital ID
     * @return HospitalDTO with specializations and doctor count
     * @throws HospitalNotFoundException if hospital not found or inactive
     */
    public HospitalDTO getHospitalById(Long id) {
        log.debug("Fetching hospital with ID: {}", id);
        
        // Use JOIN FETCH method to prevent N+1 query problem
        Hospital hospital = hospitalRepository.findByIdWithDoctors(id)
                .orElseThrow(() -> new HospitalNotFoundException(id));
        
        return toDtoWithDetails(hospital);
    }

    /**
     * Gets hospitals filtered by city.
     * 
     * @param city City name
     * @return List of hospitals in the specified city
     */
    public List<HospitalDTO> getHospitalsByCity(String city) {
        log.debug("Fetching hospitals in city: {}", city);
        
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        
        return hospitalRepository.findByCityAndIsActiveTrue(city)
                .stream()
                .map(this::toDtoWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * Gets hospitals filtered by district.
     * 
     * @param district District name
     * @return List of hospitals in the specified district
     */
    public List<HospitalDTO> getHospitalsByDistrict(String district) {
        log.debug("Fetching hospitals in district: {}", district);
        
        if (district == null || district.trim().isEmpty()) {
            throw new IllegalArgumentException("District cannot be null or empty");
        }
        
        return hospitalRepository.findByDistrictAndIsActiveTrue(district)
                .stream()
                .map(this::toDtoWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * Gets hospitals near airport within specified distance.
     * Business Logic: Validates that maxDistance is not negative.
     * 
     * @param maxDistance Maximum distance from airport in kilometers (must be non-negative)
     * @return List of hospitals within specified distance
     * @throws IllegalArgumentException if maxDistance is negative
     */
    public List<HospitalDTO> getHospitalsNearAirport(Double maxDistance) {
        log.debug("Fetching hospitals near airport within {} km", maxDistance);
        
        // Business logic validation: Distance cannot be negative
        if (maxDistance != null && maxDistance < 0) {
            throw new IllegalArgumentException("Mesafe değeri negatif olamaz. Verilen değer: " + maxDistance);
        }
        
        return hospitalRepository.findByAirportDistanceLessThanEqual(maxDistance)
                .stream()
                .map(this::toDtoWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * Health Tourism Critical: Gets hospitals by medical specialization.
     * Business Logic: Finds hospitals that have doctors in the specified specialization.
     * This is critical for health tourism - patients search by medical specialty.
     * 
     * @param specialization Medical specialization (e.g., "Cardiology", "Ophthalmology")
     * @param city Optional city filter (can be null for all cities)
     * @return List of hospitals offering the specified specialization
     */
    public List<HospitalDTO> getHospitalsBySpecialization(String specialization, String city) {
        log.info("Searching hospitals with specialization: {} in city: {}", specialization, city);
        
        // Business logic validation
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization cannot be null or empty");
        }
        
        List<Hospital> hospitals;
        
        if (city != null && !city.trim().isEmpty()) {
            // Use repository method that filters by both specialization and city
            hospitals = hospitalRepository.findHospitalsBySpecialtyAndCity(specialization, city);
        } else {
            // Search across all cities (if repository has such method)
            // For now, use the specialization search from doctors and get their hospitals
            hospitals = doctorRepository.searchBySpecialization(specialization)
                    .stream()
                    .map(d -> d.getHospital())
                    .filter(h -> h.getIsActive())
                    .distinct()
                    .collect(Collectors.toList());
        }
        
        return hospitals.stream()
                .map(this::toDtoWithDetails)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets list of all specializations available at a hospital.
     * Critical for health tourism: patients need to know what services a hospital offers.
     * 
     * @param hospitalId Hospital ID
     * @return List of unique specializations
     * @throws HospitalNotFoundException if hospital not found
     */
    public List<String> getHospitalSpecializations(Long hospitalId) {
        log.debug("Fetching specializations for hospital ID: {}", hospitalId);
        
        Hospital hospital = hospitalRepository.findByIdWithDoctors(hospitalId)
                .orElseThrow(() -> new HospitalNotFoundException(hospitalId));
        
        return hospitalMapper.extractSpecializations(hospital);
    }

    /**
     * Creates a new hospital.
     * Business Logic:
     * - Sets default values (isActive=true, rating=0, totalReviews=0)
     * - Validates business rules (duplicate name/address check can be added)
     * 
     * @param hospital Hospital entity to create
     * @return Created HospitalDTO
     */
    @Transactional // Override read-only for write operation
    public HospitalDTO createHospital(Hospital hospital) {
        log.info("Creating new hospital: {}", hospital.getName());
        
        // Business logic: Set default values
        hospital.setIsActive(true);
        hospital.setRating(0.0);
        hospital.setTotalReviews(0);
        
        // Business logic validation: Optional duplicate check
        // You can add: check if hospital with same name/address exists
        
        Hospital saved = hospitalRepository.save(hospital);
        log.info("Successfully created hospital with ID: {}", saved.getId());
        
        return toDtoWithDetails(saved);
    }
    
    /**
     * Helper method to convert Hospital entity to DTO with all details.
     * Enriches DTO with specializations, accreditations, and doctor count.
     * Uses JOIN FETCH data to prevent N+1 queries.
     * 
     * @param hospital Hospital entity (should be loaded with doctors via JOIN FETCH)
     * @return HospitalDTO with all details
     */
    private HospitalDTO toDtoWithDetails(Hospital hospital) {
        HospitalDTO dto = hospitalMapper.toDto(hospital);
        
        // Enrich DTO with specializations (from doctors)
        dto.setSpecializations(hospitalMapper.extractSpecializations(hospital));
        
        // Enrich DTO with accreditations
        dto.setAccreditations(hospital.getAccreditations());
        
        // Enrich DTO with active doctor count
        dto.setActiveDoctorCount(hospitalMapper.countActiveDoctors(hospital));
        
        return dto;
    }
}
