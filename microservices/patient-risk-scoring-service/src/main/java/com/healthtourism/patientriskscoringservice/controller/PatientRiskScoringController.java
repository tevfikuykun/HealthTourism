package com.healthtourism.patientriskscoringservice.controller;

import com.healthtourism.patientriskscoringservice.entity.PatientRiskScore;
import com.healthtourism.patientriskscoringservice.service.PatientRiskScoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient-risk-scoring")
@CrossOrigin(origins = "*")
@Tag(name = "Patient Risk Scoring", description = "AI-driven recovery score calculation")
public class PatientRiskScoringController {
    
    @Autowired
    private PatientRiskScoringService riskScoringService;
    
    @PostMapping("/calculate")
    @Operation(summary = "Calculate recovery score for a patient",
               description = "Combines IoT data, medical history, and procedure complexity")
    public ResponseEntity<PatientRiskScore> calculateScore(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long reservationId = Long.valueOf(request.get("reservationId").toString());
            
            PatientRiskScore score = riskScoringService.calculateRecoveryScore(userId, reservationId);
            return ResponseEntity.ok(score);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/user/{userId}/reservation/{reservationId}")
    @Operation(summary = "Get latest recovery score")
    public ResponseEntity<PatientRiskScore> getLatestScore(
            @PathVariable Long userId,
            @PathVariable Long reservationId) {
        PatientRiskScore score = riskScoringService.getLatestScore(userId, reservationId);
        return score != null ? ResponseEntity.ok(score) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/user/{userId}/reservation/{reservationId}/history")
    @Operation(summary = "Get score history")
    public ResponseEntity<List<PatientRiskScore>> getScoreHistory(
            @PathVariable Long userId,
            @PathVariable Long reservationId) {
        return ResponseEntity.ok(riskScoringService.getScoreHistory(userId, reservationId));
    }
    
    @GetMapping("/alerts")
    @Operation(summary = "Get scores requiring doctor alert")
    public ResponseEntity<List<PatientRiskScore>> getScoresRequiringAlert() {
        return ResponseEntity.ok(riskScoringService.getScoresRequiringAlert());
    }
}
