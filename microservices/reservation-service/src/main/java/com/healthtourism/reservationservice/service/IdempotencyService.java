package com.healthtourism.reservationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Idempotency Service
 * 
 * Prevents duplicate requests from creating multiple reservations.
 * Uses Redis to store idempotency keys.
 * 
 * Business Rule: Same request (same idempotency key) should result in same response,
 * even if called multiple times (e.g., user clicks "Reserve" button multiple times).
 */
@Service
public class IdempotencyService {
    
    private static final Logger logger = LoggerFactory.getLogger(IdempotencyService.class);
    
    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:reservation:";
    private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24); // 24 hours TTL
    
    // In production, inject RedisTemplate
    // private final RedisTemplate<String, String> redisTemplate;
    
    /**
     * Generate a new idempotency key
     * 
     * @return Generated idempotency key
     */
    public String generateIdempotencyKey() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Check if request with idempotency key was already processed
     * 
     * @param idempotencyKey Idempotency key
     * @return Optional reservation ID if already processed, empty otherwise
     */
    public Optional<Long> getExistingReservationId(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            // TODO: Implement Redis lookup
            // String reservationIdStr = redisTemplate.opsForValue().get(IDEMPOTENCY_KEY_PREFIX + idempotencyKey);
            // if (reservationIdStr != null) {
            //     Long reservationId = Long.parseLong(reservationIdStr);
            //     logger.debug("Found existing reservation for idempotency key: {} -> {}", idempotencyKey, reservationId);
            //     return Optional.of(reservationId);
            // }
            
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error checking idempotency key: {}", idempotencyKey, e);
            // Don't fail the request if idempotency check fails
            return Optional.empty();
        }
    }
    
    /**
     * Store idempotency key with reservation ID
     * 
     * @param idempotencyKey Idempotency key
     * @param reservationId Reservation ID
     */
    public void storeIdempotencyKey(String idempotencyKey, Long reservationId) {
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty() || reservationId == null) {
            return;
        }
        
        try {
            // TODO: Implement Redis storage
            // String key = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
            // redisTemplate.opsForValue().set(key, String.valueOf(reservationId), IDEMPOTENCY_TTL);
            // logger.debug("Stored idempotency key: {} -> {}", idempotencyKey, reservationId);
        } catch (Exception e) {
            logger.error("Error storing idempotency key: {}", idempotencyKey, e);
            // Don't fail the request if idempotency storage fails
        }
    }
    
    /**
     * Validate idempotency key format
     * 
     * @param idempotencyKey Key to validate
     * @return true if valid UUID format
     */
    public boolean isValidIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            return false;
        }
        
        try {
            UUID.fromString(idempotencyKey);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

