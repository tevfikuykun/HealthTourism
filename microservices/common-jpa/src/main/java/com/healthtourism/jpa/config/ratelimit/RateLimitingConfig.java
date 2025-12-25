package com.healthtourism.jpa.config.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Rate Limiting Configuration
 * 
 * Registers RateLimitingInterceptor to intercept all requests.
 */
@Configuration
public class RateLimitingConfig implements WebMvcConfigurer {
    
    @Autowired
    private RateLimitingInterceptor rateLimitingInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingInterceptor)
            .addPathPatterns("/api/**") // Apply to all /api/** endpoints
            .excludePathPatterns(
                "/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/actuator/**"
            ); // Exclude documentation and monitoring endpoints
    }
}

