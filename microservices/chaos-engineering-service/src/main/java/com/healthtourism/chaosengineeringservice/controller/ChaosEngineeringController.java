package com.healthtourism.chaosengineeringservice.controller;

import com.healthtourism.chaosengineeringservice.service.FailSafeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chaos")
@CrossOrigin(origins = "*")
@Tag(name = "Chaos Engineering", description = "Fail-safe mode and service availability testing")
public class ChaosEngineeringController {
    
    @Autowired
    private FailSafeService failSafeService;
    
    @GetMapping("/health")
    @Operation(summary = "Get system health status",
               description = "Check if critical services are available and fail-safe mode status")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        return ResponseEntity.ok(failSafeService.getSystemHealthStatus());
    }
    
    @PostMapping("/check-access")
    @Operation(summary = "Check medical data access (with fail-safe)",
               description = "Checks access authorization. Returns fail-safe response if Privacy Service is down.")
    public ResponseEntity<Map<String, Object>> checkMedicalDataAccess(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long dataOwnerId = Long.valueOf(request.get("dataOwnerId").toString());
        String accessPurpose = request.getOrDefault("accessPurpose", "TREATMENT").toString();
        
        Map<String, Object> result = failSafeService.checkMedicalDataAccess(userId, dataOwnerId, accessPurpose);
        
        if (Boolean.TRUE.equals(result.get("failSafeMode"))) {
            return ResponseEntity.status(503).body(result); // Service Unavailable
        }
        
        return ResponseEntity.ok(result);
    }
}
