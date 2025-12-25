package com.healthtourism.jpa.config;

import com.healthtourism.jpa.config.caching.ETagFilter;
import com.healthtourism.jpa.config.logging.RequestLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Enterprise Controller Configuration
 * 
 * Configures enterprise-level features for all controllers:
 * - Request Logging (security audit)
 * - ETag Filter (caching)
 * - Rate Limiting (already configured in RateLimitingConfig)
 */
@Configuration
public class EnterpriseControllerConfig implements WebMvcConfigurer {
    
    @Autowired
    private RequestLoggingInterceptor requestLoggingInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Request Logging Interceptor
        registry.addInterceptor(requestLoggingInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns("/actuator/**", "/swagger-ui/**", "/api-docs/**");
    }
    
    /**
     * Register ETag Filter for HTTP caching
     * 
     * @param eTagFilter ETag filter bean
     * @return Filter registration bean
     */
    @Bean
    public FilterRegistrationBean<ETagFilter> eTagFilterRegistration(ETagFilter eTagFilter) {
        FilterRegistrationBean<ETagFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(eTagFilter);
        registration.addUrlPatterns("/api/v1/*/get*", "/api/v1/*/list*", "/api/v1/*/my-*"); // Only GET/list endpoints
        registration.setOrder(1);
        return registration;
    }
}

