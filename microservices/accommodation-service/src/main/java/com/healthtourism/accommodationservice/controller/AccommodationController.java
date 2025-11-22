package com.healthtourism.accommodationservice.controller;
import com.healthtourism.accommodationservice.dto.AccommodationDTO;
import com.healthtourism.accommodationservice.entity.Accommodation;
import com.healthtourism.accommodationservice.service.AccommodationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
@CrossOrigin(origins = "*")
public class AccommodationController {
    @Autowired
    private AccommodationService accommodationService;
    
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<AccommodationDTO>> getAccommodationsByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(accommodationService.getAccommodationsByHospital(hospitalId));
    }
    
    @GetMapping("/hospital/{hospitalId}/near")
    public ResponseEntity<List<AccommodationDTO>> getAccommodationsNearHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(accommodationService.getAccommodationsNearHospital(hospitalId));
    }
    
    @GetMapping("/price")
    public ResponseEntity<List<AccommodationDTO>> getAccommodationsByPrice(@RequestParam(defaultValue = "1000") BigDecimal maxPrice) {
        return ResponseEntity.ok(accommodationService.getAccommodationsByPrice(maxPrice));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDTO> getAccommodationById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(accommodationService.getAccommodationById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<AccommodationDTO> createAccommodation(@RequestBody Accommodation accommodation) {
        return ResponseEntity.ok(accommodationService.createAccommodation(accommodation));
    }
}

