package com.healthtourism.common.resilience.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {
    
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50) // Open circuit after 50% failures
            .waitDurationInOpenState(Duration.ofSeconds(30)) // Wait 30s before trying again
            .slidingWindowSize(10) // Last 10 calls
            .minimumNumberOfCalls(5) // Minimum calls before calculating failure rate
            .permittedNumberOfCallsInHalfOpenState(3) // Test with 3 calls in half-open
            .build();
        
        return CircuitBreakerRegistry.of(config);
    }
    
    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.custom()
            .maxAttempts(3) // Retry 3 times
            .waitDuration(Duration.ofMillis(500)) // Wait 500ms between retries
            .retryExceptions(Exception.class) // Retry on any exception
            .build();
        
        return RetryRegistry.of(config);
    }
    
    @Bean
    public CircuitBreaker defaultCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("default");
    }
    
    @Bean
    public Retry defaultRetry(RetryRegistry registry) {
        return registry.retry("default");
    }
}

