package com.healthtourism.gamificationservice.controller;

import com.healthtourism.gamificationservice.entity.HealthToken;
import com.healthtourism.gamificationservice.service.HealthTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gamification/health-tokens")
@CrossOrigin(origins = "*")
@Tag(name = "Health Tokens", description = "Blockchain-backed health tokens for gamified rehabilitation")
public class HealthTokenController {
    
    @Autowired
    private HealthTokenService healthTokenService;
    
    @PostMapping("/rehabilitation")
    @Operation(summary = "Award tokens for rehabilitation activity")
    public ResponseEntity<HealthToken> awardRehabilitationToken(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long reservationId = Long.valueOf(request.get("reservationId").toString());
            String activityDescription = request.get("activityDescription").toString();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> proofData = (Map<String, Object>) request.get("proofData");
            
            HealthToken token = healthTokenService.awardRehabilitationToken(
                    userId, reservationId, activityDescription, proofData);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/medication-compliance")
    @Operation(summary = "Award tokens for medication compliance")
    public ResponseEntity<HealthToken> awardMedicationComplianceToken(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long reservationId = Long.valueOf(request.get("reservationId").toString());
            int daysCompliant = Integer.parseInt(request.get("daysCompliant").toString());
            
            HealthToken token = healthTokenService.awardMedicationComplianceToken(
                    userId, reservationId, daysCompliant);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/healthy-lifestyle")
    @Operation(summary = "Award tokens for healthy lifestyle (IoT data)")
    public ResponseEntity<HealthToken> awardHealthyLifestyleToken(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long reservationId = Long.valueOf(request.get("reservationId").toString());
            
            @SuppressWarnings("unchecked")
            Map<String, Object> iotData = (Map<String, Object>) request.get("iotData");
            
            HealthToken token = healthTokenService.awardHealthyLifestyleToken(
                    userId, reservationId, iotData);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{tokenId}/redeem")
    @Operation(summary = "Redeem health tokens")
    public ResponseEntity<HealthToken> redeemTokens(
            @PathVariable Long tokenId,
            @RequestBody Map<String, Object> request) {
        try {
            String redemptionType = request.get("redemptionType").toString();
            Long redemptionReservationId = Long.valueOf(request.get("redemptionReservationId").toString());
            
            HealthToken token = healthTokenService.redeemTokens(
                    tokenId, redemptionType, redemptionReservationId);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/user/{userId}/balance")
    @Operation(summary = "Get total token balance for a user")
    public ResponseEntity<Map<String, Object>> getTokenBalance(@PathVariable Long userId) {
        BigDecimal balance = healthTokenService.getTotalTokenBalance(userId);
        return ResponseEntity.ok(Map.of("userId", userId, "totalBalance", balance));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all tokens for a user")
    public ResponseEntity<List<HealthToken>> getTokensByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(healthTokenService.getTokensByUser(userId));
    }
}
