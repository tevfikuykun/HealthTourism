package com.healthtourism.accommodationservice.controller;

import com.healthtourism.accommodationservice.dto.AccommodationDTO;
import com.healthtourism.accommodationservice.entity.Accommodation;
import com.healthtourism.accommodationservice.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
@CrossOrigin(origins = "*")
@Tag(name = "Accommodation", description = "Accommodation management APIs")
public class AccommodationController {
    @Autowired
    private AccommodationService accommodationService;
    
    @GetMapping("/hospital/{hospitalId}")
    @Operation(summary = "Get accommodations by hospital ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accommodations retrieved successfully")
    })
    public ResponseEntity<List<AccommodationDTO>> getAccommodationsByHospital(
            @Parameter(description = "Hospital ID", required = true) @PathVariable Long hospitalId) {
        return ResponseEntity.ok(accommodationService.getAccommodationsByHospital(hospitalId));
    }
    
    @GetMapping("/hospital/{hospitalId}/near")
    @Operation(summary = "Get accommodations near hospital")
    public ResponseEntity<List<AccommodationDTO>> getAccommodationsNearHospital(
            @Parameter(description = "Hospital ID", required = true) @PathVariable Long hospitalId) {
        return ResponseEntity.ok(accommodationService.getAccommodationsNearHospital(hospitalId));
    }
    
    @GetMapping("/price")
    @Operation(summary = "Get accommodations by maximum price")
    public ResponseEntity<List<AccommodationDTO>> getAccommodationsByPrice(
            @Parameter(description = "Maximum price per night") @RequestParam(defaultValue = "1000") BigDecimal maxPrice) {
        return ResponseEntity.ok(accommodationService.getAccommodationsByPrice(maxPrice));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get accommodation by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accommodation found"),
        @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    public ResponseEntity<AccommodationDTO> getAccommodationById(
            @Parameter(description = "Accommodation ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(accommodationService.getAccommodationById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create a new accommodation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accommodation created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<AccommodationDTO> createAccommodation(@Valid @RequestBody Accommodation accommodation) {
        return ResponseEntity.ok(accommodationService.createAccommodation(accommodation));
    }
}

