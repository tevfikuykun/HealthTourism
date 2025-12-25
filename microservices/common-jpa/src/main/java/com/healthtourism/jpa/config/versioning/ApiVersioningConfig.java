package com.healthtourism.jpa.config.versioning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API Versioning Configuration
 * 
 * Registers ApiVersionInterceptor to handle API versioning.
 */
@Configuration
public class ApiVersioningConfig implements WebMvcConfigurer {
    
    @Autowired
    private ApiVersionInterceptor apiVersionInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiVersionInterceptor)
            .addPathPatterns("/api/**"); // Apply to all /api/** endpoints
    }
}

