package com.healthtourism.carrentalservice.controller;

import com.healthtourism.carrentalservice.dto.CarRentalDTO;
import com.healthtourism.carrentalservice.service.CarRentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/car-rentals")
@CrossOrigin(origins = "*")
@Tag(name = "Car Rental", description = "Car rental management APIs")
public class CarRentalController {
    @Autowired
    private CarRentalService carRentalService;
    
    @GetMapping
    @Operation(summary = "Get all available car rentals")
    public ResponseEntity<List<CarRentalDTO>> getAllCarRentals() {
        return ResponseEntity.ok(carRentalService.getAllAvailableCarRentals());
    }
    
    @GetMapping("/type/{carType}")
    @Operation(summary = "Get car rentals by type")
    public ResponseEntity<List<CarRentalDTO>> getCarRentalsByType(
            @Parameter(description = "Car type", required = true) @PathVariable String carType) {
        return ResponseEntity.ok(carRentalService.getCarRentalsByType(carType));
    }
    
    @GetMapping("/price")
    @Operation(summary = "Get car rentals by maximum price")
    public ResponseEntity<List<CarRentalDTO>> getCarRentalsByPrice(
            @Parameter(description = "Maximum price per day") @RequestParam(defaultValue = "1000") BigDecimal maxPrice) {
        return ResponseEntity.ok(carRentalService.getCarRentalsByPrice(maxPrice));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get car rental by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car rental found"),
        @ApiResponse(responseCode = "404", description = "Car rental not found")
    })
    public ResponseEntity<CarRentalDTO> getCarRentalById(
            @Parameter(description = "Car rental ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(carRentalService.getCarRentalById(id));
    }
}

