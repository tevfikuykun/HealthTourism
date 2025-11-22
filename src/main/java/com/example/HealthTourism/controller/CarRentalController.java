package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.CarRentalDTO;
import com.example.HealthTourism.service.CarRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/car-rentals")
@CrossOrigin(origins = "*")
public class CarRentalController {
    
    @Autowired
    private CarRentalService carRentalService;
    
    @GetMapping
    public ResponseEntity<List<CarRentalDTO>> getAllCarRentals() {
        return ResponseEntity.ok(carRentalService.getAllAvailableCarRentals());
    }
    
    @GetMapping("/type/{carType}")
    public ResponseEntity<List<CarRentalDTO>> getCarRentalsByType(@PathVariable String carType) {
        return ResponseEntity.ok(carRentalService.getCarRentalsByType(carType));
    }
    
    @GetMapping("/price")
    public ResponseEntity<List<CarRentalDTO>> getCarRentalsByPrice(
            @RequestParam(defaultValue = "1000") BigDecimal maxPrice) {
        return ResponseEntity.ok(carRentalService.getCarRentalsByPrice(maxPrice));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CarRentalDTO> getCarRentalById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(carRentalService.getCarRentalById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

