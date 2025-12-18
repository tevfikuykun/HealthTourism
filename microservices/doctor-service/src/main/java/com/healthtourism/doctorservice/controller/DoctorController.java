package com.healthtourism.doctorservice.controller;

import com.healthtourism.doctorservice.dto.DoctorDTO;
import com.healthtourism.doctorservice.entity.Doctor;
import com.healthtourism.doctorservice.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
@Tag(name = "Doctor", description = "Doctor management APIs")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;
    
    @GetMapping("/hospital/{hospitalId}")
    @Operation(summary = "Get doctors by hospital ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctors retrieved successfully")
    })
    public ResponseEntity<List<DoctorDTO>> getDoctorsByHospital(
            @Parameter(description = "Hospital ID", required = true) @PathVariable Long hospitalId) {
        return ResponseEntity.ok(doctorService.getDoctorsByHospital(hospitalId));
    }
    
    @GetMapping("/specialization/{specialization}")
    @Operation(summary = "Get doctors by specialization")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(
            @Parameter(description = "Specialization", required = true) @PathVariable String specialization) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specialization));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor found"),
        @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<DoctorDTO> getDoctorById(
            @Parameter(description = "Doctor ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }
    
    @GetMapping("/hospital/{hospitalId}/top-rated")
    @Operation(summary = "Get top-rated doctors by hospital")
    public ResponseEntity<List<DoctorDTO>> getTopRatedDoctorsByHospital(
            @Parameter(description = "Hospital ID", required = true) @PathVariable Long hospitalId) {
        return ResponseEntity.ok(doctorService.getTopRatedDoctorsByHospital(hospitalId));
    }
    
    @PostMapping
    @Operation(summary = "Create a new doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<DoctorDTO> createDoctor(@Valid @RequestBody Doctor doctor) {
        return ResponseEntity.ok(doctorService.createDoctor(doctor));
    }
    
    @PostMapping("/{id}/upload-image")
    @Operation(summary = "Upload doctor image", description = "Uploads an image for a doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid image or validation failed"),
        @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDTO> uploadDoctorImage(
            @Parameter(description = "Doctor ID", required = true) @PathVariable Long id,
            @Parameter(description = "Image file", required = true) @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.ok(doctorService.uploadDoctorImage(id, file));
    }
}

