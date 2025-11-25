package com.healthtourism.visaconsultationservice.controller;

import com.healthtourism.visaconsultationservice.dto.VisaConsultancyDTO;
import com.healthtourism.visaconsultationservice.entity.VisaConsultancy;
import com.healthtourism.visaconsultationservice.service.VisaConsultancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visa-consultancies")
@CrossOrigin(origins = "*")
public class VisaConsultancyController {
    @Autowired
    private VisaConsultancyService visaConsultancyService;
    
    @GetMapping
    public ResponseEntity<List<VisaConsultancyDTO>> getAllConsultancies() {
        return ResponseEntity.ok(visaConsultancyService.getAllAvailableConsultancies());
    }
    
    @GetMapping("/country/{country}")
    public ResponseEntity<List<VisaConsultancyDTO>> getConsultanciesByCountry(@PathVariable String country) {
        return ResponseEntity.ok(visaConsultancyService.getConsultanciesByCountry(country));
    }
    
    @GetMapping("/type/{visaType}")
    public ResponseEntity<List<VisaConsultancyDTO>> getConsultanciesByVisaType(@PathVariable String visaType) {
        return ResponseEntity.ok(visaConsultancyService.getConsultanciesByVisaType(visaType));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<VisaConsultancyDTO>> searchConsultancies(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String visaType) {
        if (country != null && visaType != null) {
            return ResponseEntity.ok(visaConsultancyService.getConsultanciesByCountryAndVisaType(country, visaType));
        } else if (country != null) {
            return ResponseEntity.ok(visaConsultancyService.getConsultanciesByCountry(country));
        } else if (visaType != null) {
            return ResponseEntity.ok(visaConsultancyService.getConsultanciesByVisaType(visaType));
        }
        return ResponseEntity.ok(visaConsultancyService.getAllAvailableConsultancies());
    }
    
    @GetMapping("/top-rated")
    public ResponseEntity<List<VisaConsultancyDTO>> getTopRatedConsultancies() {
        return ResponseEntity.ok(visaConsultancyService.getTopRatedConsultancies());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VisaConsultancyDTO> getConsultancyById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(visaConsultancyService.getConsultancyById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<VisaConsultancyDTO> createConsultancy(@RequestBody VisaConsultancy consultancy) {
        return ResponseEntity.ok(visaConsultancyService.createConsultancy(consultancy));
    }
}

