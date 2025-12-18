package com.healthtourism.apikeymanagerservice.controller;

import com.healthtourism.apikeymanagerservice.entity.ApiKey;
import com.healthtourism.apikeymanagerservice.entity.ApiUsageLog;
import com.healthtourism.apikeymanagerservice.repository.ApiKeyRepository;
import com.healthtourism.apikeymanagerservice.repository.ApiUsageLogRepository;
import com.healthtourism.apikeymanagerservice.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/api-keys")
@Tag(name = "API Key Manager", description = "API key management and usage tracking")
public class ApiKeyController {
    
    @Autowired
    private ApiKeyService apiKeyService;
    
    @Autowired
    private ApiKeyRepository apiKeyRepository;
    
    @Autowired
    private ApiUsageLogRepository usageLogRepository;
    
    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generate API key", description = "Generate a new API key for external partners")
    public ResponseEntity<ApiKey> generateApiKey(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String organization = request.get("organization");
        String plan = request.getOrDefault("plan", "BASIC");
        
        ApiKey apiKey = apiKeyService.generateApiKey(name, organization, plan);
        return ResponseEntity.ok(apiKey);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all API keys", description = "Get all API keys (Admin only)")
    public ResponseEntity<List<ApiKey>> getAllApiKeys() {
        return ResponseEntity.ok(apiKeyRepository.findAll());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get API key by ID", description = "Get API key details (Admin only)")
    public ResponseEntity<ApiKey> getApiKeyById(@PathVariable Long id) {
        return apiKeyRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update API key status", description = "Activate or deactivate API key")
    public ResponseEntity<ApiKey> updateApiKeyStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {
        return apiKeyRepository.findById(id)
            .map(apiKey -> {
                apiKey.setActive(active);
                return ResponseEntity.ok(apiKeyRepository.save(apiKey));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/usage")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get API key usage statistics", description = "Get usage statistics for an API key")
    public ResponseEntity<Map<String, Object>> getUsageStatistics(@PathVariable Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id).orElse(null);
        if (apiKey == null) {
            return ResponseEntity.notFound().build();
        }
        
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        Long monthlyRequests = usageLogRepository.countByApiKeyIdAndTimestampAfter(id, startOfMonth);
        Double avgResponseTime = usageLogRepository.getAverageResponseTime(id, startOfMonth);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("apiKeyId", id);
        stats.put("totalRequests", apiKey.getTotalRequests());
        stats.put("monthlyRequests", monthlyRequests);
        stats.put("averageResponseTime", avgResponseTime != null ? avgResponseTime : 0);
        stats.put("rateLimit", apiKey.getRateLimit());
        stats.put("plan", apiKey.getPlan());
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/{id}/logs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get API usage logs", description = "Get detailed usage logs for an API key")
    public ResponseEntity<List<ApiUsageLog>> getUsageLogs(@PathVariable Long id) {
        return ResponseEntity.ok(usageLogRepository.findByApiKeyId(id));
    }
    
    @PostMapping("/validate")
    @Operation(summary = "Validate API key", description = "Validate API key and check rate limit")
    public ResponseEntity<Map<String, Object>> validateApiKey(
            @RequestParam String apiKey,
            @RequestParam String endpoint) {
        
        boolean valid = apiKeyService.validateApiKey(apiKey, endpoint);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", valid);
        response.put("message", valid ? "API key is valid" : "API key is invalid or rate limit exceeded");
        
        return ResponseEntity.ok(response);
    }
}
