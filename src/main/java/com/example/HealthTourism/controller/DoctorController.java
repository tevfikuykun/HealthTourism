package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.DoctorDTO;
import com.example.HealthTourism.entity.Doctor;
import com.example.HealthTourism.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Doctor Controller
 * Handles doctor management endpoints
 * 
 * Error handling is delegated to GlobalExceptionHandler:
 * - DoctorNotFoundException -> 404 NOT_FOUND
 * - IllegalArgumentException -> 400 BAD_REQUEST
 * - MethodArgumentNotValidException -> 400 BAD_REQUEST (validation errors)
 * 
 * Security Note: In production, @PostMapping endpoint should be restricted to
 * ADMIN or HOSPITAL_MANAGER roles using @PreAuthorize or Security Filter Chain.
 * 
 * Future Enhancement: Consider using DoctorCreateRequestDTO instead of Doctor entity
 * in createDoctor() method to avoid exposing database schema to the API layer.
 */
@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {
    
    private final DoctorService doctorService;
    
    /**
     * Get all doctors by hospital ID
     * GET /api/v1/doctors/hospital/{hospitalId}
     */
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(doctorService.getDoctorsByHospital(hospitalId));
    }
    
    /**
     * Get doctors by specialization
     * GET /api/v1/doctors/search?specialization=Kardiyoloji
     * 
     * Note: Using @RequestParam instead of @PathVariable to handle multi-word
     * specializations (e.g., "Saç Ekimi", "Plastik Cerrahi") with proper URL encoding.
     * 
     * @param specialization Specialization to search for (case-insensitive flexible search)
     */
    @GetMapping("/search")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(
            @RequestParam String specialization) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specialization));
    }
    
    /**
     * Get doctor by ID
     * GET /api/v1/doctors/{id}
     * 
     * Error handling is delegated to GlobalExceptionHandler
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }
    
    /**
     * Get top-rated doctors by hospital
     * GET /api/v1/doctors/hospital/{hospitalId}/top-rated
     */
    @GetMapping("/hospital/{hospitalId}/top-rated")
    public ResponseEntity<List<DoctorDTO>> getTopRatedDoctorsByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(doctorService.getTopRatedDoctorsByHospital(hospitalId));
    }
    
    /**
     * Create a new doctor
     * POST /api/v1/doctors
     * 
     * Security: This endpoint should be restricted to ADMIN or HOSPITAL_MANAGER roles.
     * 
     * Future Enhancement: Consider using DoctorCreateRequestDTO instead of Doctor entity
     * to improve API security and avoid exposing internal database schema.
     * 
     * @param doctor Doctor entity to create
     * @return Created doctor with 201 CREATED status
     */
    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@Valid @RequestBody Doctor doctor) {
        DoctorDTO created = doctorService.createDoctor(doctor);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}

