package com.healthtourism.hospitalservice.controller;

import com.healthtourism.hospitalservice.dto.HospitalDTO;
import com.healthtourism.hospitalservice.dto.HospitalRequestDTO;
import com.healthtourism.hospitalservice.entity.Hospital;
import com.healthtourism.hospitalservice.service.AdvancedSearchService;
import com.healthtourism.hospitalservice.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hospitals")
@CrossOrigin(origins = "*")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;
    
    @Autowired
    private AdvancedSearchService advancedSearchService;
    
    @GetMapping
    public ResponseEntity<List<HospitalDTO>> getAllHospitals() {
        return ResponseEntity.ok(hospitalService.getAllActiveHospitals());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<HospitalDTO> getHospitalById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(hospitalService.getHospitalById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<HospitalDTO>> getHospitalsByCity(@PathVariable String city) {
        return ResponseEntity.ok(hospitalService.getHospitalsByCity(city));
    }
    
    @GetMapping("/district/{district}")
    public ResponseEntity<List<HospitalDTO>> getHospitalsByDistrict(@PathVariable String district) {
        return ResponseEntity.ok(hospitalService.getHospitalsByDistrict(district));
    }
    
    @GetMapping("/near-airport")
    public ResponseEntity<List<HospitalDTO>> getHospitalsNearAirport(
            @RequestParam(defaultValue = "50.0") Double maxDistance) {
        return ResponseEntity.ok(hospitalService.getHospitalsNearAirport(maxDistance));
    }
    
    @PostMapping
    public ResponseEntity<HospitalDTO> createHospital(@Valid @RequestBody HospitalRequestDTO request) {
        Hospital hospital = new Hospital();
        hospital.setName(request.getName());
        hospital.setAddress(request.getAddress());
        hospital.setCity(request.getCity());
        hospital.setAirportDistance(request.getAirportDistance());
        hospital.setRating(request.getRating());
        hospital.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        return ResponseEntity.ok(hospitalService.createHospital(hospital));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HospitalDTO> updateHospital(@PathVariable Long id, @Valid @RequestBody HospitalRequestDTO request) {
        Hospital hospital = new Hospital();
        hospital.setName(request.getName());
        hospital.setAddress(request.getAddress());
        hospital.setCity(request.getCity());
        hospital.setAirportDistance(request.getAirportDistance());
        hospital.setRating(request.getRating());
        hospital.setIsActive(request.getIsActive());
        return ResponseEntity.ok(hospitalService.updateHospital(id, hospital));
    }
    
    // Advanced Search
    @GetMapping("/search/advanced")
    public ResponseEntity<List<HospitalDTO>> advancedSearch(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Double maxDistanceFromAirport,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String treatmentType,
            @RequestParam(required = false) Boolean hasVisaSupport,
            @RequestParam(required = false) Boolean hasTranslationService) {
        
        List<Hospital> hospitals = advancedSearchService.advancedSearch(
            city, minRating, maxDistanceFromAirport, specialization,
            treatmentType, hasVisaSupport, hasTranslationService
        );
        
        List<HospitalDTO> dtos = hospitals.stream()
            .map(hospitalService::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @PostMapping("/search/criteria")
    public ResponseEntity<List<HospitalDTO>> searchByCriteria(@RequestBody Map<String, Object> criteria) {
        List<Hospital> hospitals = advancedSearchService.searchByMultipleCriteria(criteria);
        
        List<HospitalDTO> dtos = hospitals.stream()
            .map(hospitalService::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @PostMapping("/{id}/upload-image")
    @Operation(summary = "Upload hospital image", description = "Uploads an image for a hospital")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid image or validation failed"),
        @ApiResponse(responseCode = "404", description = "Hospital not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HospitalDTO> uploadHospitalImage(
            @Parameter(description = "Hospital ID", required = true) @PathVariable Long id,
            @Parameter(description = "Image file", required = true) @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(hospitalService.uploadHospitalImage(id, file));
    }
}

