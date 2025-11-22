package com.healthtourism.insuranceservice.controller;
import com.healthtourism.insuranceservice.dto.InsuranceDTO;
import com.healthtourism.insuranceservice.service.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/insurance")
@CrossOrigin(origins = "*")
public class InsuranceController {
    @Autowired
    private InsuranceService insuranceService;
    
    @GetMapping
    public ResponseEntity<List<InsuranceDTO>> getAllInsurances() {
        return ResponseEntity.ok(insuranceService.getAllActiveInsurances());
    }
    
    @GetMapping("/type/{insuranceType}")
    public ResponseEntity<List<InsuranceDTO>> getInsurancesByType(@PathVariable String insuranceType) {
        return ResponseEntity.ok(insuranceService.getInsurancesByType(insuranceType));
    }
    
    @GetMapping("/coverage/{coverageArea}")
    public ResponseEntity<List<InsuranceDTO>> getInsurancesByCoverageArea(@PathVariable String coverageArea) {
        return ResponseEntity.ok(insuranceService.getInsurancesByCoverageArea(coverageArea));
    }
    
    @GetMapping("/premium")
    public ResponseEntity<List<InsuranceDTO>> getInsurancesByPremium(@RequestParam(defaultValue = "1000") BigDecimal maxPremium) {
        return ResponseEntity.ok(insuranceService.getInsurancesByPremium(maxPremium));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InsuranceDTO> getInsuranceById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(insuranceService.getInsuranceById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

