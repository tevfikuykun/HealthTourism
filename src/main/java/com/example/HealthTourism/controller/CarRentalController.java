package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.CarRentalDTO;
import com.example.HealthTourism.service.CarRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Car Rental Controller
 * Handles car rental management endpoints
 * 
 * Error handling is delegated to GlobalExceptionHandler:
 * - RuntimeException -> 400 BAD_REQUEST (fallback)
 * 
 * Future Enhancement: Consider adding a combined search endpoint (/search)
 * that accepts multiple optional filters (type, minPassengers, maxPrice) for
 * more flexible querying, especially useful for health tourism needs
 * (e.g., wheelchair-accessible vehicles with specific capacity requirements).
 */
@RestController
@RequestMapping("/api/v1/car-rentals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CarRentalController {
    
    private final CarRentalService carRentalService;
    
    /**
     * Get all available car rentals
     * GET /api/v1/car-rentals
     */
    @GetMapping
    public ResponseEntity<List<CarRentalDTO>> getAllCarRentals() {
        return ResponseEntity.ok(carRentalService.getAllAvailableCarRentals());
    }
    
    /**
     * Get car rentals by type
     * GET /api/v1/car-rentals/type/{carType}
     * 
     * @param carType Car type (e.g., "SUV", "Sedan", "Luxury", "Van")
     *                Note: Consider using Enum for type validation in future
     */
    @GetMapping("/type/{carType}")
    public ResponseEntity<List<CarRentalDTO>> getCarRentalsByType(@PathVariable String carType) {
        return ResponseEntity.ok(carRentalService.getCarRentalsByType(carType));
    }
    
    /**
     * Get car rentals by maximum price per day
     * GET /api/v1/car-rentals/price?maxPrice=1000
     * 
     * @param maxPrice Maximum price per day (default: 1000)
     */
    @GetMapping("/price")
    public ResponseEntity<List<CarRentalDTO>> getCarRentalsByPrice(
            @RequestParam(defaultValue = "1000") BigDecimal maxPrice) {
        return ResponseEntity.ok(carRentalService.getCarRentalsByPrice(maxPrice));
    }
    
    /**
     * Get car rental by ID
     * GET /api/v1/car-rentals/{id}
     * 
     * Error handling is delegated to GlobalExceptionHandler
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarRentalDTO> getCarRentalById(@PathVariable Long id) {
        return ResponseEntity.ok(carRentalService.getCarRentalById(id));
    }
}

