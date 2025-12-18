package com.healthtourism.authservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis-based Refresh Token Service
 * Stores refresh tokens in Redis for better performance and security
 */
@Service
public class RedisRefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${jwt.refresh-expiration:2592000000}") // 30 days default
    private Long refreshExpirationMs;
    
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String USER_REFRESH_TOKENS_PREFIX = "user_refresh_tokens:";

    @Autowired
    public RedisRefreshTokenService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Generate and store a new refresh token
     */
    public String generateRefreshToken(Long userId, String deviceId) {
        String token = UUID.randomUUID().toString();
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("userId", userId);
        tokenData.put("deviceId", deviceId);
        tokenData.put("createdAt", LocalDateTime.now().toString());
        tokenData.put("isRevoked", false);
        
        // Store token with expiration
        String tokenKey = REFRESH_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(tokenKey, tokenData, refreshExpirationMs, TimeUnit.MILLISECONDS);
        
        // Track user's refresh tokens for revocation
        String userTokensKey = USER_REFRESH_TOKENS_PREFIX + userId;
        redisTemplate.opsForSet().add(userTokensKey, token);
        redisTemplate.expire(userTokensKey, refreshExpirationMs, TimeUnit.MILLISECONDS);
        
        return token;
    }

    /**
     * Validate refresh token
     */
    public Map<String, Object> validateRefreshToken(String token) {
        String tokenKey = REFRESH_TOKEN_PREFIX + token;
        Object tokenDataObj = redisTemplate.opsForValue().get(tokenKey);
        
        if (tokenDataObj == null) {
            return null; // Token not found or expired
        }
        
        try {
            Map<String, Object> tokenData = objectMapper.convertValue(tokenDataObj, 
                new TypeReference<Map<String, Object>>() {});
            
            Boolean isRevoked = (Boolean) tokenData.get("isRevoked");
            if (Boolean.TRUE.equals(isRevoked)) {
                return null; // Token revoked
            }
            
            return tokenData;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Revoke a specific refresh token
     */
    public void revokeToken(String token) {
        String tokenKey = REFRESH_TOKEN_PREFIX + token;
        Object tokenDataObj = redisTemplate.opsForValue().get(tokenKey);
        
        if (tokenDataObj != null) {
            try {
                Map<String, Object> tokenData = objectMapper.convertValue(tokenDataObj, 
                    new TypeReference<Map<String, Object>>() {});
                tokenData.put("isRevoked", true);
                redisTemplate.opsForValue().set(tokenKey, tokenData);
            } catch (Exception e) {
                // If update fails, just delete the token
                redisTemplate.delete(tokenKey);
            }
        }
    }

    /**
     * Revoke all refresh tokens for a user
     */
    public void revokeAllUserTokens(Long userId) {
        String userTokensKey = USER_REFRESH_TOKENS_PREFIX + userId;
        Set<Object> tokens = redisTemplate.opsForSet().members(userTokensKey);
        
        if (tokens != null) {
            for (Object tokenObj : tokens) {
                String token = tokenObj.toString();
                revokeToken(token);
            }
        }
        
        redisTemplate.delete(userTokensKey);
    }

    /**
     * Delete expired tokens (cleanup job)
     */
    public void cleanupExpiredTokens() {
        // Redis automatically expires keys, but we can also clean up user token sets
        // This would typically be called by a scheduled task
    }
}

