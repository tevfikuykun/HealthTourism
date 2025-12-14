package com.healthtourism.airecommendationservice.controller;
import com.healthtourism.airecommendationservice.service.AIRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIRecommendationController {
    @Autowired
    private AIRecommendationService service;

    @GetMapping("/recommendations")
    public ResponseEntity<List<Map<String, Object>>> getRecommendations(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(service.getRecommendations(params));
    }
}

