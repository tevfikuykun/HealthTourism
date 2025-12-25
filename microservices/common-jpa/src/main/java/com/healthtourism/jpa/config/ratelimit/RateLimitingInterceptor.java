package com.healthtourism.jpa.config.ratelimit;

import com.healthtourism.jpa.config.SecurityContextHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Rate Limiting Interceptor
 * 
 * Intercepts HTTP requests and applies rate limiting based on @RateLimited annotation.
 * Uses Redis for distributed rate limiting (works across multiple instances).
 * 
 * Rate limiting is applied before the request reaches the controller,
 * preventing bot attacks and ensuring fair resource usage.
 */
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingInterceptor.class);
    
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    
    @Value("${security.ratelimit.enabled:true}")
    private boolean rateLimitingEnabled;
    
    @Value("${security.ratelimit.default-requests-per-minute:100}")
    private int defaultRequestsPerMinute;
    
    private static final String RATE_LIMIT_PREFIX = "rate_limit:";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!rateLimitingEnabled) {
            return true; // Rate limiting disabled
        }
        
        if (!(handler instanceof HandlerMethod)) {
            return true; // Not a controller method
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // Check for @RateLimited annotation on method
        RateLimited rateLimited = handlerMethod.getMethodAnnotation(RateLimited.class);
        
        // Check for @RateLimited annotation on controller class
        if (rateLimited == null) {
            rateLimited = handlerMethod.getBeanType().getAnnotation(RateLimited.class);
        }
        
        // If no annotation, use default rate limit
        if (rateLimited == null) {
            return checkRateLimit(request, response, defaultRequestsPerMinute, 60, true);
        }
        
        return checkRateLimit(
            request,
            response,
            rateLimited.requestsPerMinute(),
            rateLimited.windowSeconds(),
            rateLimited.perUser(),
            rateLimited.errorMessage()
        );
    }
    
    /**
     * Check rate limit with default error message
     */
    private boolean checkRateLimit(
            HttpServletRequest request,
            HttpServletResponse response,
            int requestsPerMinute,
            int windowSeconds,
            boolean perUser) throws IOException {
        
        return checkRateLimit(request, response, requestsPerMinute, windowSeconds, perUser,
            "Rate limit exceeded. Please try again later.");
    }
    
    /**
     * Check rate limit
     */
    private boolean checkRateLimit(
            HttpServletRequest request,
            HttpServletResponse response,
            int requestsPerMinute,
            int windowSeconds,
            boolean perUser,
            String errorMessage) throws IOException {
        
        String rateLimitKey = buildRateLimitKey(request, perUser);
        
        // If Redis is not available, allow request (fallback)
        if (redisTemplate == null) {
            logger.warn("Redis not available, rate limiting disabled");
            return true;
        }
        
        // Check rate limit using Redis
        String redisKey = RATE_LIMIT_PREFIX + rateLimitKey;
        Long currentCount = redisTemplate.opsForValue().increment(redisKey);
        
        // Set expiration on first request
        if (currentCount == 1) {
            redisTemplate.expire(redisKey, windowSeconds, TimeUnit.SECONDS);
        }
        
        // Check if limit exceeded
        if (currentCount > requestsPerMinute) {
            logger.warn("Rate limit exceeded for key: {} (count: {})", rateLimitKey, currentCount);
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            // Add rate limit headers
            response.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + windowSeconds));
            
            // Write error response
            PrintWriter writer = response.getWriter();
            writer.write(String.format("""
                {
                  "success": false,
                  "status": 429,
                  "errorCode": "RATE_LIMIT_EXCEEDED",
                  "message": "%s",
                  "timestamp": "%s"
                }
                """, errorMessage, java.time.LocalDateTime.now()));
            writer.flush();
            
            return false; // Stop request processing
        }
        
        // Add rate limit headers
        response.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, requestsPerMinute - currentCount)));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + windowSeconds));
        
        return true; // Allow request
    }
    
    /**
     * Build rate limit key
     */
    private String buildRateLimitKey(HttpServletRequest request, boolean perUser) {
        String baseKey = request.getRequestURI();
        
        if (perUser) {
            // Try to get user ID from SecurityContext
            try {
                UUID userId = SecurityContextHelper.getCurrentUserId();
                return baseKey + ":" + userId;
            } catch (Exception e) {
                // Fallback to IP if user not authenticated
                String ip = getClientIp(request);
                return baseKey + ":" + ip;
            }
        } else {
            // Rate limit by IP
            String ip = getClientIp(request);
            return baseKey + ":" + ip;
        }
    }
    
    /**
     * Get client IP address
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

