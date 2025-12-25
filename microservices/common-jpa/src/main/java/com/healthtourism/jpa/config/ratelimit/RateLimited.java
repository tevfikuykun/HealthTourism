package com.healthtourism.jpa.config.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rate Limited Annotation
 * 
 * Marks endpoints or controllers with rate limiting configuration.
 * Rate limiting is applied via RateLimitingInterceptor.
 * 
 * Usage:
 * <pre>
 * {@code
 * @RateLimited(requestsPerMinute = 60)
 * @GetMapping("/endpoint")
 * public ResponseEntity<?> endpoint() {
 *     // ...
 * }
 * }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    
    /**
     * Maximum number of requests allowed per minute
     * Default: 100 requests per minute
     */
    int requestsPerMinute() default 100;
    
    /**
     * Time window in seconds
     * Default: 60 seconds (1 minute)
     */
    int windowSeconds() default 60;
    
    /**
     * Rate limit key prefix (for Redis key)
     * Default: "rate_limit"
     */
    String keyPrefix() default "rate_limit";
    
    /**
     * Whether to include user ID in rate limit key
     * If true, rate limit is per user; if false, per IP
     * Default: true (per user)
     */
    boolean perUser() default true;
    
    /**
     * Custom error message when rate limit is exceeded
     * Default: "Rate limit exceeded. Please try again later."
     */
    String errorMessage() default "Rate limit exceeded. Please try again later.";
}

