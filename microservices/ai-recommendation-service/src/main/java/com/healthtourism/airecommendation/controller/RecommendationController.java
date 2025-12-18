package com.healthtourism.airecommendation.controller;

import com.healthtourism.airecommendation.dto.RecommendationRequest;
import com.healthtourism.airecommendation.dto.RecommendationResponse;
import com.healthtourism.airecommendation.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    @PostMapping
    public ResponseEntity<RecommendationResponse> getRecommendations(@RequestBody RecommendationRequest request) {
        return ResponseEntity.ok(recommendationService.getRecommendations(request));
    }
}
