package com.healthtourism.chaosengineeringservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Fail-Safe Service
 * Implements fail-safe mode when critical services are down
 * Privacy Service down → All medical data access denied safely
 */
@Service
public class FailSafeService {
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    @Value("${privacy.compliance.service.url:http://localhost:8038}")
    private String privacyServiceUrl;
    
    @Value("${fail.safe.enabled:true}")
    private boolean failSafeEnabled;
    
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
    
    /**
     * Check if Privacy Service is available
     * If not, enter fail-safe mode
     */
    public boolean isPrivacyServiceAvailable() {
        if (!failSafeEnabled) {
            return true; // Fail-safe disabled, assume service is available
        }
        
        try {
            String url = privacyServiceUrl + "/actuator/health";
            ResponseEntity<Map> response = getRestTemplate().getForEntity(url, Map.class);
            return response.getStatusCode().is2xxSuccessful() && 
                   "UP".equals(response.getBody().get("status"));
        } catch (Exception e) {
            System.err.println("Privacy Service unavailable. Entering fail-safe mode.");
            return false;
        }
    }
    
    /**
     * Fail-safe check for medical data access
     * Returns fail-safe response if Privacy Service is down
     */
    public Map<String, Object> checkMedicalDataAccess(Long userId, Long dataOwnerId, String accessPurpose) {
        if (!isPrivacyServiceAvailable()) {
            // Fail-safe mode: Deny all access when Privacy Service is down
            Map<String, Object> failSafeResponse = new HashMap<>();
            failSafeResponse.put("allowed", false);
            failSafeResponse.put("reason", "PRIVACY_SERVICE_UNAVAILABLE");
            failSafeResponse.put("message", "Medical data access is temporarily unavailable due to privacy service maintenance. Please try again later.");
            failSafeResponse.put("failSafeMode", true);
            return failSafeResponse;
        }
        
        // Normal mode: Check authorization via Privacy Service
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("userId", userId);
            request.put("dataOwnerId", dataOwnerId);
            request.put("accessPurpose", accessPurpose);
            
            String url = privacyServiceUrl + "/api/privacy/verify-access";
            Map<String, Object> response = getRestTemplate().postForObject(url, request, Map.class);
            
            return response != null ? response : createDefaultDeniedResponse();
        } catch (Exception e) {
            // If Privacy Service check fails, enter fail-safe mode
            return createFailSafeDeniedResponse();
        }
    }
    
    /**
     * Fail-safe response for wallet access
     */
    public Map<String, Object> createFailSafeWalletResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("available", false);
        response.put("failSafeMode", true);
        response.put("message", "Health wallet service is temporarily unavailable. Please try again later.");
        response.put("reason", "SERVICE_UNAVAILABLE");
        return response;
    }
    
    private Map<String, Object> createDefaultDeniedResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("allowed", false);
        response.put("reason", "AUTHORIZATION_FAILED");
        return response;
    }
    
    private Map<String, Object> createFailSafeDeniedResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("allowed", false);
        response.put("reason", "PRIVACY_SERVICE_UNAVAILABLE");
        response.put("message", "Privacy service is unavailable. Access denied for security.");
        response.put("failSafeMode", true);
        return response;
    }
    
    /**
     * Get system health status (for monitoring)
     */
    public Map<String, Object> getSystemHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        
        boolean privacyAvailable = isPrivacyServiceAvailable();
        health.put("privacyService", privacyAvailable ? "UP" : "DOWN");
        health.put("failSafeMode", !privacyAvailable);
        health.put("failSafeEnabled", failSafeEnabled);
        
        return health;
    }
}
