package com.healthtourism.insuranceservice.controller;

import com.healthtourism.insuranceservice.entity.InsurancePolicy;
import com.healthtourism.insuranceservice.service.SmartInsuranceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/insurance/smart")
@CrossOrigin(origins = "*")
@Tag(name = "Smart Insurance", description = "Blockchain-backed medical travel insurance policies")
public class SmartInsuranceController {
    
    @Autowired
    private SmartInsuranceService smartInsuranceService;
    
    @PostMapping("/policy")
    @Operation(summary = "Create blockchain-backed insurance policy", 
               description = "Creates a smart insurance policy with blockchain verification")
    public ResponseEntity<InsurancePolicy> createSmartPolicy(
            @RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long insuranceId = Long.valueOf(request.get("insuranceId").toString());
            Long reservationId = Long.valueOf(request.get("reservationId").toString());
            LocalDateTime startDate = LocalDateTime.parse(request.get("startDate").toString());
            LocalDateTime endDate = LocalDateTime.parse(request.get("endDate").toString());
            
            InsurancePolicy policy = smartInsuranceService.createSmartPolicy(
                    userId, insuranceId, reservationId, startDate, endDate);
            return ResponseEntity.ok(policy);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/policy/user/{userId}")
    @Operation(summary = "Get all policies for a user")
    public ResponseEntity<List<InsurancePolicy>> getPoliciesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(smartInsuranceService.getPoliciesByUser(userId));
    }
    
    @GetMapping("/policy/reservation/{reservationId}")
    @Operation(summary = "Get policy by reservation ID")
    public ResponseEntity<InsurancePolicy> getPolicyByReservation(@PathVariable Long reservationId) {
        try {
            return ResponseEntity.ok(smartInsuranceService.getPolicyByReservation(reservationId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/policy/{policyId}/verify")
    @Operation(summary = "Verify policy integrity using blockchain")
    public ResponseEntity<Map<String, Object>> verifyPolicy(@PathVariable Long policyId) {
        boolean isValid = smartInsuranceService.verifyPolicyIntegrity(policyId);
        return ResponseEntity.ok(Map.of("policyId", policyId, "isValid", isValid));
    }
    
    @PostMapping("/policy/{policyId}/claim")
    @Operation(summary = "Claim insurance for post-op complications",
               description = "Creates a claim for post-operative complications with blockchain record")
    public ResponseEntity<InsurancePolicy> claimInsurance(
            @PathVariable Long policyId,
            @RequestBody Map<String, Object> claimRequest) {
        try {
            String claimReason = claimRequest.get("claimReason").toString();
            BigDecimal claimAmount = new BigDecimal(claimRequest.get("claimAmount").toString());
            
            InsurancePolicy policy = smartInsuranceService.claimInsurance(
                    policyId, claimReason, claimAmount);
            return ResponseEntity.ok(policy);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
