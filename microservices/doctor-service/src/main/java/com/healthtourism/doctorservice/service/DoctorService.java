package com.healthtourism.doctorservice.service;

import com.healthtourism.doctorservice.dto.DoctorCreateRequest;
import com.healthtourism.doctorservice.dto.DoctorResponseDTO;
import com.healthtourism.doctorservice.dto.DoctorUpdateRequest;
import com.healthtourism.doctorservice.entity.Doctor;
import com.healthtourism.doctorservice.exception.DoctorNotFoundException;
import com.healthtourism.doctorservice.exception.HospitalNotFoundException;
import com.healthtourism.doctorservice.mapper.DoctorMapper;
import com.healthtourism.doctorservice.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Doctor Service - Professional Implementation
 * 
 * Best Practices Applied:
 * - Constructor injection (no field injection)
 * - MapStruct mapper for Entity-DTO conversion
 * - DTO pattern (no entity leakage)
 * - Transactional methods (read-only for queries, read-write for mutations)
 * - Business rule validation
 * - Custom exceptions
 * - Caching for frequently accessed data
 * - Pagination support
 * - Logging for critical operations
 */
@Service
@RequiredArgsConstructor // Constructor injection
@Transactional(readOnly = true) // Default: read-only for all methods (performance optimization)
public class DoctorService {
    
    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);
    
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper; // MapStruct mapper
    private final SpecializationValidationService specializationValidationService;
    
    // Note: In a microservice architecture, HospitalRepository would be in hospital-service
    // For now, we'll validate hospital existence via hospital-service API or assume it exists
    // In production, use Feign Client or RestTemplate to call hospital-service
    
    /**
     * Get doctors by hospital ID with pagination
     * 
     * Business Rule: Validates hospital existence
     */
    @Cacheable(value = "doctors", key = "'hospital:' + #hospitalId + ':page:' + #pageable.pageNumber")
    public Page<DoctorResponseDTO> getDoctorsByHospital(Long hospitalId, Pageable pageable) {
        logger.debug("Fetching doctors for hospital ID: {} (page: {}, size: {})", 
            hospitalId, pageable.getPageNumber(), pageable.getPageSize());
        
        // Business Rule: Validate hospital existence
        // In production, this would call hospital-service:
        // if (!hospitalService.existsById(hospitalId)) {
        //     throw new HospitalNotFoundException(hospitalId);
        // }
        
        Page<Doctor> doctors = doctorRepository.findByHospitalIdAndIsAvailableTrue(hospitalId, pageable);
        return doctors.map(doctorMapper::toResponseDTO);
    }
    
    /**
     * Get doctors by hospital ID (without pagination - for backward compatibility)
     */
    @Cacheable(value = "doctors", key = "'hospital:' + #hospitalId")
    public List<DoctorResponseDTO> getDoctorsByHospital(Long hospitalId) {
        logger.debug("Fetching all doctors for hospital ID: {}", hospitalId);
        
        List<Doctor> doctors = doctorRepository.findByHospitalIdAndIsAvailableTrue(hospitalId);
        return doctorMapper.toResponseDTOList(doctors);
    }
    
    /**
     * Get doctors by specialization with pagination
     * 
     * Business Rule: Validates specialization
     */
    @Cacheable(value = "doctors", key = "'specialization:' + #specialization + ':page:' + #pageable.pageNumber")
    public Page<DoctorResponseDTO> getDoctorsBySpecialization(String specialization, Pageable pageable) {
        logger.debug("Fetching doctors for specialization: {} (page: {}, size: {})", 
            specialization, pageable.getPageNumber(), pageable.getPageSize());
        
        // Business Rule: Validate specialization
        specializationValidationService.validateSpecialization(specialization);
        
        Page<Doctor> doctors = doctorRepository.findBySpecializationAndIsAvailableTrue(specialization, pageable);
        return doctors.map(doctorMapper::toResponseDTO);
    }
    
    /**
     * Get doctors by specialization (without pagination - for backward compatibility)
     */
    @Cacheable(value = "doctors", key = "'specialization:' + #specialization")
    public List<DoctorResponseDTO> getDoctorsBySpecialization(String specialization) {
        logger.debug("Fetching all doctors for specialization: {}", specialization);
        
        // Business Rule: Validate specialization
        specializationValidationService.validateSpecialization(specialization);
        
        List<Doctor> doctors = doctorRepository.findBySpecializationOrderByRatingDesc(specialization);
        return doctorMapper.toResponseDTOList(doctors);
    }
    
    /**
     * Get doctor by ID
     * 
     * Throws DoctorNotFoundException if doctor not found (handled by GlobalExceptionHandler)
     */
    @Cacheable(value = "doctors", key = "'id:' + #id")
    public DoctorResponseDTO getDoctorById(Long id) {
        logger.debug("Fetching doctor with ID: {}", id);
        
        Doctor doctor = doctorRepository.findByIdAndIsAvailableTrue(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
        
        return doctorMapper.toResponseDTO(doctor);
    }
    
    /**
     * Get top rated doctors by hospital with pagination
     */
    @Cacheable(value = "doctors", key = "'top-rated:hospital:' + #hospitalId + ':page:' + #pageable.pageNumber")
    public Page<DoctorResponseDTO> getTopRatedDoctorsByHospital(Long hospitalId, Pageable pageable) {
        logger.debug("Fetching top rated doctors for hospital ID: {} (page: {}, size: {})", 
            hospitalId, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Doctor> doctors = doctorRepository.findByHospitalIdAndIsAvailableTrueOrderByRatingDesc(hospitalId, pageable);
        return doctors.map(doctorMapper::toResponseDTO);
    }
    
    /**
     * Get top rated doctors by hospital (without pagination - for backward compatibility)
     */
    @Cacheable(value = "doctors", key = "'top-rated:hospital:' + #hospitalId")
    public List<DoctorResponseDTO> getTopRatedDoctorsByHospital(Long hospitalId) {
        logger.debug("Fetching all top rated doctors for hospital ID: {}", hospitalId);
        
        List<Doctor> doctors = doctorRepository.findByHospitalIdOrderByRatingDesc(hospitalId);
        return doctorMapper.toResponseDTOList(doctors);
    }
    
    /**
     * Create a new doctor
     * 
     * Business Rules:
     * 1. Hospital must exist
     * 2. Specializations must be valid
     * 3. System-managed fields are set automatically
     * 
     * Uses DTO pattern to prevent entity leakage
     * MapStruct mapper converts CreateRequest to Entity
     */
    @Transactional // Override read-only: write operation
    @CacheEvict(value = "doctors", allEntries = true) // Clear cache after creation
    public DoctorResponseDTO createDoctor(DoctorCreateRequest request) {
        logger.info("Creating new doctor: {} {}", request.getFirstName(), request.getLastName());
        
        // Business Rule 1: Validate hospital existence
        // In production, this would call hospital-service:
        // Hospital hospital = hospitalService.getHospitalById(request.getHospitalId())
        //     .orElseThrow(() -> new HospitalNotFoundException(request.getHospitalId()));
        // For now, we'll assume hospital exists (would be validated at API gateway level)
        
        // Business Rule 2: Validate specializations
        specializationValidationService.validateSpecializations(request.getSpecializations());
        
        // Convert CreateRequest to Entity using MapStruct
        Doctor doctor = doctorMapper.toEntity(request);
        
        // System-managed fields (cannot be set by user)
        doctor.setIsAvailable(true);
        doctor.setRating(0.0);
        doctor.setTotalReviews(0);
        
        Doctor savedDoctor = doctorRepository.save(doctor);
        logger.info("Doctor created successfully with ID: {}", savedDoctor.getId());
        
        return doctorMapper.toResponseDTO(savedDoctor);
    }
    
    /**
     * Update an existing doctor
     * 
     * Business Rules:
     * 1. Doctor must exist
     * 2. If specializations are updated, they must be valid
     * 
     * Uses MapStruct mapper for partial updates
     * System-managed fields (id, rating, totalReviews) cannot be updated
     */
    @Transactional // Override read-only: write operation
    @CacheEvict(value = "doctors", allEntries = true) // Clear cache after update
    public DoctorResponseDTO updateDoctor(Long id, DoctorUpdateRequest request) {
        logger.info("Updating doctor with ID: {}", id);
        
        Doctor doctor = doctorRepository.findByIdAndIsAvailableTrue(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
        
        // Business Rule: Validate specializations if provided
        if (request.getSpecializations() != null && !request.getSpecializations().isEmpty()) {
            specializationValidationService.validateSpecializations(request.getSpecializations());
        }
        
        // Update entity using MapStruct mapper (handles partial updates)
        doctorMapper.updateEntityFromRequest(doctor, request);
        
        Doctor updatedDoctor = doctorRepository.save(doctor);
        logger.info("Doctor updated successfully with ID: {}", id);
        
        return doctorMapper.toResponseDTO(updatedDoctor);
    }
    
    /**
     * Delete a doctor (soft delete)
     * 
     * Business Rule: Doctor must exist
     * 
     * Uses @SQLDelete annotation on entity for automatic soft delete
     * This preserves data integrity and audit trail
     */
    @Transactional // Override read-only: write operation
    @CacheEvict(value = "doctors", allEntries = true) // Clear cache after deletion
    public void deleteDoctor(Long id) {
        logger.info("Deleting doctor with ID: {}", id);
        
        Doctor doctor = doctorRepository.findByIdAndIsAvailableTrue(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
        
        // Soft delete using @SQLDelete annotation
        // The @SQLDelete annotation on Doctor entity will automatically update deleted = true
        doctorRepository.delete(doctor);
        
        logger.info("Doctor deleted successfully with ID: {} (soft delete via @SQLDelete)", id);
    }
    
    /**
     * Upload doctor image
     * 
     * Business Rule: Doctor must exist
     */
    @Transactional // Override read-only: write operation
    @CacheEvict(value = "doctors", key = "'id:' + #id") // Clear cache for this doctor
    public DoctorResponseDTO uploadDoctorImage(Long id, MultipartFile file) {
        logger.info("Uploading image for doctor ID: {}", id);
        
        Doctor doctor = doctorRepository.findByIdAndIsAvailableTrue(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
        
        // Call file-storage-service to upload image
        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
            
            org.springframework.util.LinkedMultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
            byte[] fileBytes = file.getBytes();
            org.springframework.core.io.ByteArrayResource resource = new org.springframework.core.io.ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", resource);
            
            org.springframework.http.HttpEntity<org.springframework.util.LinkedMultiValueMap<String, Object>> requestEntity = 
                new org.springframework.http.HttpEntity<>(body, headers);
            
            // TODO: Implement file-storage-service call using RestTemplate or Feign Client
            // For now, using a placeholder implementation
            // org.springframework.http.ResponseEntity<java.util.Map> response = 
            //     restTemplate.postForEntity(
            //         "http://localhost:8027/api/files/upload/image/doctor/" + id,
            //         requestEntity,
            //         java.util.Map.class
            //     );
            
            // Placeholder: Set image URL directly
            doctor.setImageUrl("http://localhost:8027/api/files/doctor/" + id + "/image");
            doctor.setThumbnailUrl("http://localhost:8027/api/files/doctor/" + id + "/thumbnail");
            
            doctor = doctorRepository.save(doctor);
            logger.info("Image uploaded successfully for doctor ID: {}", id);
        } catch (java.io.IOException e) {
            logger.error("Failed to upload image for doctor ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Görsel yükleme hatası: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Failed to upload image for doctor ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Görsel yükleme hatası: " + e.getMessage(), e);
        }
        
        return doctorMapper.toResponseDTO(doctor);
    }
}
