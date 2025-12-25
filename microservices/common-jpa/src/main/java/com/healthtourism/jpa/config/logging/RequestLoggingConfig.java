package com.healthtourism.jpa.config.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Request Logging Configuration
 * 
 * Registers RequestLoggingInterceptor to log all HTTP requests.
 */
@Configuration
public class RequestLoggingConfig implements WebMvcConfigurer {
    
    @Autowired
    private RequestLoggingInterceptor requestLoggingInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
            .addPathPatterns("/api/**") // Apply to all /api/** endpoints
            .excludePathPatterns("/actuator/**", "/swagger-ui/**", "/api-docs/**"); // Exclude monitoring endpoints
    }
}

