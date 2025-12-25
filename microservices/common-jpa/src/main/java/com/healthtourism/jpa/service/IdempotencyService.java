package com.healthtourism.jpa.service;

import java.time.Duration;
import java.util.Optional;

/**
 * Idempotency Service
 * 
 * Prevents duplicate operations by tracking idempotency keys.
 * Used for financial operations (payments, reservations) to ensure
 * the same request is not processed multiple times.
 * 
 * Implementation:
 * - Store idempotency key with operation result
 * - Check if key exists before processing
 * - Return cached result if key exists
 * - Expire keys after a certain period (e.g., 24 hours)
 * 
 * Storage:
 * - Redis (recommended for distributed systems)
 * - In-memory cache (for single-instance applications)
 * - Database (for persistence)
 */
public interface IdempotencyService {
    
    /**
     * Check if idempotency key exists (operation already processed)
     * 
     * @param key Idempotency key
     * @return true if key exists (operation already processed)
     */
    boolean exists(String key);
    
    /**
     * Get cached result for idempotency key
     * 
     * @param key Idempotency key
     * @return Cached result (if exists)
     */
    Optional<Object> getCachedResult(String key);
    
    /**
     * Store idempotency key with operation result
     * 
     * @param key Idempotency key
     * @param result Operation result
     * @param expiration Expiration duration (e.g., 24 hours)
     */
    void store(String key, Object result, Duration expiration);
    
    /**
     * Store idempotency key with default expiration (24 hours)
     * 
     * @param key Idempotency key
     * @param result Operation result
     */
    default void store(String key, Object result) {
        store(key, result, Duration.ofHours(24));
    }
    
    /**
     * Delete idempotency key (manual cleanup)
     * 
     * @param key Idempotency key
     */
    void delete(String key);
}

