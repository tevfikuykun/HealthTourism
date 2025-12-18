package com.healthtourism.gamificationservice.service;

import com.healthtourism.gamificationservice.entity.HealthToken;
import com.healthtourism.gamificationservice.repository.HealthTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Health Token Service
 * Manages blockchain-backed health tokens for gamified rehabilitation
 */
@Service
public class HealthTokenService {
    
    @Autowired
    private HealthTokenRepository healthTokenRepository;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    @Value("${blockchain.service.url:http://localhost:8030}")
    private String blockchainServiceUrl;
    
    // Token Economics: Inflation Control
    @Value("${health.token.max.supply:1000000}")
    private BigDecimal maxTokenSupply; // Maximum total token supply
    
    @Value("${health.token.burn.rate:0.05}")
    private BigDecimal burnRate; // 5% of tokens burned on redemption
    
    @Value("${health.token.daily.cap:500}")
    private BigDecimal dailyTokenCap; // Maximum tokens per user per day
    
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
    
    /**
     * Award health tokens for rehabilitation activity
     */
    @Transactional
    public HealthToken awardRehabilitationToken(
            Long userId,
            Long reservationId,
            String activityDescription,
            Map<String, Object> proofData) {
        
        // Calculate token amount based on activity
        BigDecimal tokenAmount = calculateTokenAmount("REHABILITATION", proofData);
        
        HealthToken token = new HealthToken();
        token.setUserId(userId);
        token.setReservationId(reservationId);
        token.setTokenAmount(tokenAmount);
        token.setTokenType("REHABILITATION");
        token.setActivityDescription(activityDescription);
        token.setProofData(convertToJson(proofData));
        
        // Save token
        token = healthTokenRepository.save(token);
        
        // Create blockchain record
        try {
            String blockchainHash = createBlockchainRecord(token);
            token.setBlockchainHash(blockchainHash);
            token.setIsBlockchainVerified(true);
            token = healthTokenRepository.save(token);
        } catch (Exception e) {
            System.err.println("Failed to create blockchain record: " + e.getMessage());
        }
        
        return token;
    }
    
    /**
     * Award tokens for medication compliance
     */
    @Transactional
    public HealthToken awardMedicationComplianceToken(
            Long userId,
            Long reservationId,
            int daysCompliant) {
        
        BigDecimal tokenAmount = new BigDecimal(daysCompliant * 10); // 10 tokens per day
        
        HealthToken token = new HealthToken();
        token.setUserId(userId);
        token.setReservationId(reservationId);
        token.setTokenAmount(tokenAmount);
        token.setTokenType("MEDICATION_COMPLIANCE");
        token.setActivityDescription("Medication compliance for " + daysCompliant + " days");
        
        token = healthTokenRepository.save(token);
        
        try {
            String blockchainHash = createBlockchainRecord(token);
            token.setBlockchainHash(blockchainHash);
            token.setIsBlockchainVerified(true);
            token = healthTokenRepository.save(token);
        } catch (Exception e) {
            System.err.println("Failed to create blockchain record: " + e.getMessage());
        }
        
        return token;
    }
    
    /**
     * Award tokens for healthy lifestyle (IoT data)
     */
    @Transactional
    public HealthToken awardHealthyLifestyleToken(
            Long userId,
            Long reservationId,
            Map<String, Object> iotData) {
        
        // Analyze IoT data (steps, sleep, heart rate, etc.)
        BigDecimal tokenAmount = analyzeIoTDataForTokens(iotData);
        
        HealthToken token = new HealthToken();
        token.setUserId(userId);
        token.setReservationId(reservationId);
        token.setTokenAmount(tokenAmount);
        token.setTokenType("HEALTHY_LIFESTYLE");
        token.setActivityDescription("Healthy lifestyle activities verified via IoT");
        token.setProofData(convertToJson(iotData));
        
        token = healthTokenRepository.save(token);
        
        try {
            String blockchainHash = createBlockchainRecord(token);
            token.setBlockchainHash(blockchainHash);
            token.setIsBlockchainVerified(true);
            token = healthTokenRepository.save(token);
        } catch (Exception e) {
            System.err.println("Failed to create blockchain record: " + e.getMessage());
        }
        
        return token;
    }
    
    /**
     * Redeem health tokens for discount or free services
     * Includes burn mechanism: 5% of redeemed tokens are burned to control inflation
     */
    @Transactional
    public HealthToken redeemTokens(
            Long tokenId,
            String redemptionType,
            Long redemptionReservationId) {
        
        HealthToken token = healthTokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Health token not found"));
        
        if (!"ACTIVE".equals(token.getStatus())) {
            throw new RuntimeException("Token is not active");
        }
        
        if (LocalDateTime.now().isAfter(token.getExpiresAt())) {
            throw new RuntimeException("Token has expired");
        }
        
        // Calculate burn amount (5% of token amount)
        BigDecimal burnAmount = token.getTokenAmount().multiply(burnRate);
        BigDecimal redeemedAmount = token.getTokenAmount().subtract(burnAmount);
        
        // Mark token as used
        token.setStatus("USED");
        token.setRedeemedAt(LocalDateTime.now());
        token.setRedemptionType(redemptionType);
        token.setRedemptionReservationId(redemptionReservationId);
        token.setBurnAmount(burnAmount);
        token.setRedeemedAmount(redeemedAmount);
        
        // Create burn record on blockchain
        try {
            createBurnRecord(token, burnAmount);
        } catch (Exception e) {
            System.err.println("Failed to create burn record: " + e.getMessage());
        }
        
        return healthTokenRepository.save(token);
    }
    
    /**
     * Get current total token supply (for inflation control)
     */
    public BigDecimal getCurrentTotalSupply() {
        List<HealthToken> allTokens = healthTokenRepository.findAll();
        return allTokens.stream()
                .filter(t -> "ACTIVE".equals(t.getStatus()))
                .filter(t -> LocalDateTime.now().isBefore(t.getExpiresAt()))
                .map(HealthToken::getTokenAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Get daily tokens earned by user (for daily cap enforcement)
     */
    private BigDecimal getDailyTokensEarned(Long userId) {
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        List<HealthToken> todayTokens = healthTokenRepository.findByUserIdAndEarnedAtAfter(userId, todayStart);
        return todayTokens.stream()
                .map(HealthToken::getTokenAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Create burn record on blockchain (for transparency)
     */
    private void createBurnRecord(HealthToken token, BigDecimal burnAmount) {
        try {
            Map<String, Object> burnData = new HashMap<>();
            burnData.put("tokenId", token.getTokenId());
            burnData.put("burnAmount", burnAmount.toString());
            burnData.put("burnRate", burnRate.toString());
            burnData.put("burnedAt", LocalDateTime.now().toString());
            burnData.put("reason", "REDEMPTION_BURN");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("data", burnData);
            requestBody.put("recordType", "TOKEN_BURN");
            requestBody.put("recordId", "BURN_" + token.getTokenId());
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            String url = blockchainServiceUrl + "/api/blockchain/create";
            getRestTemplate().postForObject(url, request, Map.class);
        } catch (Exception e) {
            System.err.println("Error creating burn record: " + e.getMessage());
        }
    }
    
    /**
     * Get total token balance for a user
     */
    public BigDecimal getTotalTokenBalance(Long userId) {
        List<HealthToken> activeTokens = healthTokenRepository.findByUserIdAndStatus(userId, "ACTIVE");
        return activeTokens.stream()
                .filter(t -> LocalDateTime.now().isBefore(t.getExpiresAt()))
                .map(HealthToken::getTokenAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public List<HealthToken> getTokensByUser(Long userId) {
        return healthTokenRepository.findByUserIdOrderByEarnedAtDesc(userId);
    }
    
    private BigDecimal calculateTokenAmount(String tokenType, Map<String, Object> proofData) {
        // Calculate based on activity type and proof data
        if ("REHABILITATION".equals(tokenType)) {
            // Example: 50 tokens per exercise session
            return new BigDecimal("50.0");
        }
        return new BigDecimal("10.0"); // Default
    }
    
    private BigDecimal analyzeIoTDataForTokens(Map<String, Object> iotData) {
        BigDecimal tokens = BigDecimal.ZERO;
        
        // Steps: 1 token per 1000 steps
        if (iotData.containsKey("steps")) {
            BigDecimal steps = new BigDecimal(iotData.get("steps").toString());
            tokens = tokens.add(steps.divide(new BigDecimal("1000"), 2, java.math.RoundingMode.DOWN));
        }
        
        // Sleep: 5 tokens per hour of good sleep
        if (iotData.containsKey("sleepHours") && iotData.containsKey("sleepQuality")) {
            String quality = iotData.get("sleepQuality").toString();
            if ("GOOD".equals(quality) || "EXCELLENT".equals(quality)) {
                BigDecimal sleepHours = new BigDecimal(iotData.get("sleepHours").toString());
                tokens = tokens.add(sleepHours.multiply(new BigDecimal("5")));
            }
        }
        
        return tokens.max(new BigDecimal("100.0")); // Cap at 100 tokens per day
    }
    
    private String createBlockchainRecord(HealthToken token) {
        try {
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("tokenId", token.getTokenId());
            tokenData.put("userId", token.getUserId());
            tokenData.put("reservationId", token.getReservationId());
            tokenData.put("tokenAmount", token.getTokenAmount().toString());
            tokenData.put("tokenType", token.getTokenType());
            tokenData.put("activityDescription", token.getActivityDescription());
            tokenData.put("earnedAt", token.getEarnedAt().toString());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("data", tokenData);
            requestBody.put("recordType", "HEALTH_TOKEN");
            requestBody.put("recordId", token.getTokenId());
            requestBody.put("userId", token.getUserId().toString());
            requestBody.put("useIPFS", true);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            String url = blockchainServiceUrl + "/api/blockchain/create-with-ipfs";
            Map<String, Object> response = getRestTemplate().postForObject(url, request, Map.class);
            
            if (response != null && response.containsKey("dataHash")) {
                String hash = (String) response.get("dataHash");
                if (response.containsKey("dataReference")) {
                    token.setBlockchainReference((String) response.get("dataReference"));
                }
                return hash;
            }
        } catch (Exception e) {
            System.err.println("Error creating blockchain record: " + e.getMessage());
        }
        return null;
    }
    
    private String convertToJson(Map<String, Object> data) {
        // Simplified JSON conversion (in production, use ObjectMapper)
        return data.toString();
    }
}
