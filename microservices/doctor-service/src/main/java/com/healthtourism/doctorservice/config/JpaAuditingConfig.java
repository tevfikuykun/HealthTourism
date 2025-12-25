package com.healthtourism.doctorservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA Auditing Configuration
 * 
 * Enables automatic population of audit fields (createdAt, updatedAt, createdBy, updatedBy).
 * 
 * If using Spring Security, the current user will be automatically captured.
 * Otherwise, it falls back to a default value or system user.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditorAware();
    }
    
    /**
     * AuditorAware implementation
     * Attempts to get current user from Spring Security context
     * Falls back to "system" if no user is authenticated
     */
    public static class SpringSecurityAuditorAware implements AuditorAware<String> {
        
        @Override
        public Optional<String> getCurrentAuditor() {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                
                if (authentication == null || !authentication.isAuthenticated()) {
                    return Optional.of("system");
                }
                
                // Try to get username
                Object principal = authentication.getPrincipal();
                if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                    return Optional.of(((org.springframework.security.core.userdetails.UserDetails) principal).getUsername());
                }
                
                // Fallback to principal name
                String username = principal.toString();
                return Optional.of(username);
            } catch (Exception e) {
                // If Spring Security is not configured, return system user
                return Optional.of("system");
            }
        }
    }
}

