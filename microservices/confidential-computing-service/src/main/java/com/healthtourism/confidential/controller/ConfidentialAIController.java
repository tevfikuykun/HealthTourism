package com.healthtourism.confidential.controller;

import com.healthtourism.confidential.ConfidentialAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Confidential AI Controller
 * Zero-Knowledge AI endpoints
 */
@RestController
@RequestMapping("/api/confidential/ai")
public class ConfidentialAIController {
    
    @Autowired
    private ConfidentialAIService confidentialAIService;
    
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPatientData(
            @RequestBody Map<String, Object> patientData) {
        
        // Process in enclave (Zero-Knowledge)
        Map<String, Object> result = confidentialAIService.processPatientDataSecurely(patientData);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/attestation/verify")
    public ResponseEntity<Map<String, Object>> verifyAttestation() {
        boolean verified = confidentialAIService.verifyEnclaveAttestation();
        
        return ResponseEntity.ok(Map.of(
            "verified", verified,
            "enclaveMode", "AWS_NITRO",
            "zeroKnowledge", true
        ));
    }
}



