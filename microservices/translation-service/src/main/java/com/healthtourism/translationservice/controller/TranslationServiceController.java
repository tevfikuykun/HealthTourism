package com.healthtourism.translationservice.controller;

import com.healthtourism.translationservice.dto.TranslationServiceDTO;
import com.healthtourism.translationservice.entity.TranslationServiceEntity;
import com.healthtourism.translationservice.service.TranslationServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/translation-services")
@CrossOrigin(origins = "*")
public class TranslationServiceController {
    @Autowired
    private TranslationServiceService translationServiceService;
    
    @GetMapping
    public ResponseEntity<List<TranslationServiceDTO>> getAllServices() {
        return ResponseEntity.ok(translationServiceService.getAllAvailableServices());
    }
    
    @GetMapping("/type/{serviceType}")
    public ResponseEntity<List<TranslationServiceDTO>> getServicesByType(@PathVariable String serviceType) {
        return ResponseEntity.ok(translationServiceService.getServicesByType(serviceType));
    }
    
    @GetMapping("/certified")
    public ResponseEntity<List<TranslationServiceDTO>> getCertifiedTranslators() {
        return ResponseEntity.ok(translationServiceService.getCertifiedTranslators());
    }
    
    @GetMapping("/hospital")
    public ResponseEntity<List<TranslationServiceDTO>> getHospitalTranslators() {
        return ResponseEntity.ok(translationServiceService.getHospitalTranslators());
    }
    
    @GetMapping("/consultation")
    public ResponseEntity<List<TranslationServiceDTO>> getConsultationTranslators() {
        return ResponseEntity.ok(translationServiceService.getConsultationTranslators());
    }
    
    @GetMapping("/language/{language}")
    public ResponseEntity<List<TranslationServiceDTO>> getTranslatorsByLanguage(@PathVariable String language) {
        return ResponseEntity.ok(translationServiceService.getTranslatorsByLanguage(language));
    }
    
    @GetMapping("/top-rated")
    public ResponseEntity<List<TranslationServiceDTO>> getTopRatedTranslators() {
        return ResponseEntity.ok(translationServiceService.getTopRatedTranslators());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TranslationServiceDTO> getServiceById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(translationServiceService.getServiceById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<TranslationServiceDTO> createService(@RequestBody TranslationServiceEntity service) {
        return ResponseEntity.ok(translationServiceService.createService(service));
    }
}

