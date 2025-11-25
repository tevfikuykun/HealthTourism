package com.healthtourism.promotionservice.controller;

import com.healthtourism.promotionservice.dto.PromotionDTO;
import com.healthtourism.promotionservice.dto.PromotionValidationRequest;
import com.healthtourism.promotionservice.dto.PromotionValidationResponse;
import com.healthtourism.promotionservice.entity.Promotion;
import com.healthtourism.promotionservice.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(origins = "*")
public class PromotionController {
    @Autowired
    private PromotionService promotionService;
    
    @GetMapping
    public ResponseEntity<List<PromotionDTO>> getAllActivePromotions() {
        return ResponseEntity.ok(promotionService.getAllActivePromotions());
    }
    
    @GetMapping("/service-type/{serviceType}")
    public ResponseEntity<List<PromotionDTO>> getPromotionsByServiceType(@PathVariable String serviceType) {
        return ResponseEntity.ok(promotionService.getPromotionsByServiceType(serviceType));
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<PromotionDTO> getPromotionByCode(@PathVariable String code) {
        try {
            return ResponseEntity.ok(promotionService.getPromotionByCode(code));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<PromotionValidationResponse> validatePromotion(@RequestBody PromotionValidationRequest request) {
        return ResponseEntity.ok(promotionService.validatePromotion(request));
    }
    
    @PostMapping("/apply/{code}")
    public ResponseEntity<PromotionDTO> applyPromotion(@PathVariable String code) {
        try {
            return ResponseEntity.ok(promotionService.applyPromotion(code));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<PromotionDTO> createPromotion(@RequestBody Promotion promotion) {
        return ResponseEntity.ok(promotionService.createPromotion(promotion));
    }
}

