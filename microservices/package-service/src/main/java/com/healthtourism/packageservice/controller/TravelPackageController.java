package com.healthtourism.packageservice.controller;
import com.healthtourism.packageservice.dto.TravelPackageDTO;
import com.healthtourism.packageservice.service.TravelPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/packages")
@CrossOrigin(origins = "*")
public class TravelPackageController {
    @Autowired
    private TravelPackageService travelPackageService;
    
    @GetMapping
    public ResponseEntity<List<TravelPackageDTO>> getAllPackages() {
        return ResponseEntity.ok(travelPackageService.getAllActivePackages());
    }
    
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<TravelPackageDTO>> getPackagesByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(travelPackageService.getPackagesByHospital(hospitalId));
    }
    
    @GetMapping("/type/{packageType}")
    public ResponseEntity<List<TravelPackageDTO>> getPackagesByType(@PathVariable String packageType) {
        return ResponseEntity.ok(travelPackageService.getPackagesByType(packageType));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TravelPackageDTO> getPackageById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(travelPackageService.getPackageById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

