package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.AccommodationDTO;
import com.example.HealthTourism.entity.Accommodation;
import com.example.HealthTourism.service.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accommodations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AccommodationController {
    
    private final AccommodationService accommodationService;
    
    // Hastaneye göre filtreleme
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<AccommodationDTO>> getByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(accommodationService.getAccommodationsByHospital(hospitalId));
    }
    
    // Mesafe bazlı filtreleme
    @GetMapping("/hospital/{hospitalId}/near")
    public ResponseEntity<List<AccommodationDTO>> getNearBy(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(accommodationService.getAccommodationsNearHospital(hospitalId));
    }
    
    @GetMapping("/hospital/{hospitalId}/top-rated")
    public ResponseEntity<List<AccommodationDTO>> getTopRatedAccommodationsByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(accommodationService.getTopRatedAccommodationsByHospital(hospitalId));
    }
    
    @GetMapping("/price")
    public ResponseEntity<List<AccommodationDTO>> getAccommodationsByPrice(
            @RequestParam(defaultValue = "1000") BigDecimal maxPrice) {
        return ResponseEntity.ok(accommodationService.getAccommodationsByPrice(maxPrice));
    }
    
    // ID ile tekil getirme (Hata yönetimi Global Handler'a devredildi)
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(accommodationService.getAccommodationById(id));
    }
    
    // Yeni konaklama ekleme
    @PostMapping
    public ResponseEntity<AccommodationDTO> create(@Valid @RequestBody Accommodation accommodation) {
        AccommodationDTO created = accommodationService.createAccommodation(accommodation);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}

