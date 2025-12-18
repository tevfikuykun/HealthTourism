package com.healthtourism.security.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Advanced Rate Limiter with multiple strategies
 * - Request count limiting
 * - Payload size limiting
 * - Sliding window algorithm
 */
@Service
public class AdvancedRateLimiter {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";
    private static final String PAYLOAD_LIMIT_PREFIX = "payload_limit:";

    /**
     * Check if request is allowed based on request count
     */
    public boolean isRequestAllowed(String key, int maxRequests, int windowSeconds) {
        if (redisTemplate == null) return true; // Redis not available

        String redisKey = RATE_LIMIT_PREFIX + key;
        Long currentCount = redisTemplate.opsForValue().increment(redisKey);
        
        if (currentCount == 1) {
            redisTemplate.expire(redisKey, windowSeconds, TimeUnit.SECONDS);
        }

        return currentCount <= maxRequests;
    }

    /**
     * Check if payload size is within limits for the time window
     */
    public boolean isPayloadAllowed(String key, long payloadSize, long maxTotalBytes, int windowSeconds) {
        if (redisTemplate == null) return true;

        String redisKey = PAYLOAD_LIMIT_PREFIX + key;
        Long currentTotal = redisTemplate.opsForValue().increment(redisKey, payloadSize);
        
        if (currentTotal == payloadSize) {
            // First request in window
            redisTemplate.expire(redisKey, windowSeconds, TimeUnit.SECONDS);
        }

        return currentTotal <= maxTotalBytes;
    }

    /**
     * Sliding window rate limiting (more accurate)
     */
    public boolean isSlidingWindowAllowed(String key, int maxRequests, int windowSeconds) {
        if (redisTemplate == null) return true;

        long now = System.currentTimeMillis();
        long windowStart = now - (windowSeconds * 1000L);
        String redisKey = RATE_LIMIT_PREFIX + "sliding:" + key;

        // Remove old entries
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);

        // Count current window
        Long count = redisTemplate.opsForZSet().count(redisKey, windowStart, now);
        
        if (count == null || count < maxRequests) {
            // Add current request
            redisTemplate.opsForZSet().add(redisKey, String.valueOf(now), now);
            redisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds));
            return true;
        }

        return false;
    }

    /**
     * Get remaining requests in current window
     */
    public int getRemainingRequests(String key, int maxRequests) {
        if (redisTemplate == null) return maxRequests;

        String redisKey = RATE_LIMIT_PREFIX + key;
        Object value = redisTemplate.opsForValue().get(redisKey);
        
        if (value == null) return maxRequests;
        
        int used = Integer.parseInt(value.toString());
        return Math.max(0, maxRequests - used);
    }

    /**
     * Get remaining payload allowance
     */
    public long getRemainingPayload(String key, long maxTotalBytes) {
        if (redisTemplate == null) return maxTotalBytes;

        String redisKey = PAYLOAD_LIMIT_PREFIX + key;
        Object value = redisTemplate.opsForValue().get(redisKey);
        
        if (value == null) return maxTotalBytes;
        
        long used = Long.parseLong(value.toString());
        return Math.max(0, maxTotalBytes - used);
    }

    /**
     * Block a key (for detected attacks)
     */
    public void blockKey(String key, int blockSeconds) {
        if (redisTemplate == null) return;

        String redisKey = "blocked:" + key;
        redisTemplate.opsForValue().set(redisKey, "blocked", blockSeconds, TimeUnit.SECONDS);
    }

    /**
     * Check if key is blocked
     */
    public boolean isBlocked(String key) {
        if (redisTemplate == null) return false;

        String redisKey = "blocked:" + key;
        return redisTemplate.hasKey(redisKey);
    }
}

