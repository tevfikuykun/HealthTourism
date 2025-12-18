package com.healthtourism.apikeymanagerservice.service;

import com.healthtourism.apikeymanagerservice.entity.ApiKey;
import com.healthtourism.apikeymanagerservice.entity.ApiUsageLog;
import com.healthtourism.apikeymanagerservice.repository.ApiKeyRepository;
import com.healthtourism.apikeymanagerservice.repository.ApiUsageLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

/**
 * API Key Management Service
 * Manages API keys, rate limiting, and usage tracking
 */
@Service
public class ApiKeyService {
    
    @Autowired
    private ApiKeyRepository apiKeyRepository;
    
    @Autowired
    private ApiUsageLogRepository usageLogRepository;
    
    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;
    
    /**
     * Generate new API key
     */
    public ApiKey generateApiKey(String name, String organization, String plan) {
        ApiKey apiKey = new ApiKey();
        apiKey.setName(name);
        apiKey.setOrganization(organization);
        apiKey.setPlan(plan);
        apiKey.setActive(true);
        apiKey.setCreatedAt(LocalDateTime.now());
        
        // Set rate limit based on plan
        switch (plan.toUpperCase()) {
            case "FREE":
                apiKey.setRateLimit(100L); // 100 requests/minute
                apiKey.setExpiresAt(LocalDateTime.now().plusMonths(1));
                break;
            case "BASIC":
                apiKey.setRateLimit(1000L); // 1000 requests/minute
                apiKey.setExpiresAt(LocalDateTime.now().plusMonths(6));
                break;
            case "PREMIUM":
                apiKey.setRateLimit(10000L); // 10000 requests/minute
                apiKey.setExpiresAt(LocalDateTime.now().plusYears(1));
                break;
            case "ENTERPRISE":
                apiKey.setRateLimit(100000L); // 100000 requests/minute
                apiKey.setExpiresAt(null); // Never expires
                break;
        }
        
        // Generate API key value
        String keyValue = generateKeyValue();
        apiKey.setKeyValue(keyValue);
        
        apiKey.setTotalRequests(0L);
        apiKey.setMonthlyRequests(0L);
        
        return apiKeyRepository.save(apiKey);
    }
    
    /**
     * Validate API key and check rate limit
     */
    public boolean validateApiKey(String apiKeyValue, String endpoint) {
        ApiKey apiKey = apiKeyRepository.findByKeyValue(apiKeyValue);
        
        if (apiKey == null || !apiKey.getActive()) {
            return false;
        }
        
        // Check expiration
        if (apiKey.getExpiresAt() != null && apiKey.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        // Check rate limit using Redis
        if (redisTemplate != null) {
            String redisKey = "api_key_rate_limit:" + apiKey.getId();
            String currentCount = redisTemplate.opsForValue().get(redisKey);
            
            if (currentCount != null && Long.parseLong(currentCount) >= apiKey.getRateLimit()) {
                return false; // Rate limit exceeded
            }
            
            // Increment counter
            redisTemplate.opsForValue().increment(redisKey);
            redisTemplate.expire(redisKey, java.time.Duration.ofMinutes(1));
        }
        
        // Check allowed endpoints
        if (apiKey.getAllowedEndpoints() != null && !apiKey.getAllowedEndpoints().isEmpty()) {
            String[] allowed = apiKey.getAllowedEndpoints().split(",");
            boolean allowedEndpoint = false;
            for (String allowedPath : allowed) {
                if (endpoint.startsWith(allowedPath.trim())) {
                    allowedEndpoint = true;
                    break;
                }
            }
            if (!allowedEndpoint) {
                return false; // Endpoint not allowed
            }
        }
        
        return true;
    }
    
    /**
     * Log API usage
     */
    public void logUsage(Long apiKeyId, String endpoint, String method, 
                        Integer statusCode, Long responseTime, String ipAddress) {
        ApiUsageLog log = new ApiUsageLog();
        log.setApiKeyId(apiKeyId);
        log.setEndpoint(endpoint);
        log.setMethod(method);
        log.setStatusCode(statusCode);
        log.setResponseTime(responseTime);
        log.setTimestamp(LocalDateTime.now());
        log.setIpAddress(ipAddress);
        
        usageLogRepository.save(log);
        
        // Update API key statistics
        ApiKey apiKey = apiKeyRepository.findById(apiKeyId).orElse(null);
        if (apiKey != null) {
            apiKey.setTotalRequests(apiKey.getTotalRequests() + 1);
            apiKey.setMonthlyRequests(apiKey.getMonthlyRequests() + 1);
            apiKeyRepository.save(apiKey);
        }
    }
    
    /**
     * Reset monthly request count (scheduled)
     */
    @Scheduled(cron = "0 0 0 1 * ?") // First day of every month
    public void resetMonthlyRequests() {
        apiKeyRepository.findAll().forEach(apiKey -> {
            apiKey.setMonthlyRequests(0L);
            apiKeyRepository.save(apiKey);
        });
    }
    
    /**
     * Generate secure API key value
     */
    private String generateKeyValue() {
        // Generate UUID-based key
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis());
        String combined = uuid + timestamp;
        
        // Base64 encode
        return "ht_" + Base64.getEncoder().encodeToString(combined.getBytes())
            .replace("=", "")
            .substring(0, 40); // Limit to 40 characters
    }
}
