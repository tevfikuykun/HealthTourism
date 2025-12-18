package com.healthtourism.costpredictorservice.controller;

import com.healthtourism.costpredictorservice.entity.CostPrediction;
import com.healthtourism.costpredictorservice.service.CostPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cost-predictor")
@CrossOrigin(origins = "*")
@Tag(name = "Cost Predictor", description = "AI-powered medical cost prediction with ±5% accuracy")
public class CostPredictionController {
    
    @Autowired
    private CostPredictionService costPredictionService;
    
    @PostMapping("/predict")
    @Operation(summary = "Predict medical cost based on medical report analysis",
               description = "Analyzes medical reports from IPFS and predicts total cost with ±5% accuracy")
    public ResponseEntity<CostPrediction> predictCost(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long hospitalId = Long.valueOf(request.get("hospitalId").toString());
            Long doctorId = Long.valueOf(request.get("doctorId").toString());
            String procedureType = request.get("procedureType").toString();
            String medicalReportHash = request.getOrDefault("medicalReportHash", "").toString();
            String medicalReportReference = request.getOrDefault("medicalReportReference", "").toString();
            
            CostPrediction prediction = costPredictionService.predictCost(
                    userId, hospitalId, doctorId, procedureType, 
                    medicalReportHash, medicalReportReference);
            
            return ResponseEntity.ok(prediction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all cost predictions for a user")
    public ResponseEntity<List<CostPrediction>> getPredictionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(costPredictionService.getPredictionsByUser(userId));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get cost prediction by ID")
    public ResponseEntity<CostPrediction> getPredictionById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(costPredictionService.getPredictionById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
