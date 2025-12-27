package com.example.HealthTourism.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA Auditing Configuration
 * Entity'lerde otomatik audit field'ları (createdDate, lastModifiedDate, createdBy, lastModifiedBy) ekler
 * 
 * Özellikler:
 * - @CreatedDate: Oluşturulma tarihi (otomatik)
 * - @LastModifiedDate: Son güncelleme tarihi (otomatik)
 * - @CreatedBy: Oluşturan kullanıcı (Spring Security context'inden)
 * - @LastModifiedBy: Son güncelleyen kullanıcı (Spring Security context'inden)
 * 
 * Kullanım:
 * Entity'ler BaseEntity'yi extend ederek bu özellikleri kazanır.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {
    
    /**
     * Auditor Provider
     * Spring Security context'inden kullanıcı bilgisini alır
     * 
     * JWT token'dan gelen email bilgisi kullanılır.
     * Eğer authentication yoksa (anonymous user), "SYSTEM" döner.
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("SYSTEM");
            }
            
            // JWT token'dan gelen email bilgisi
            String email = authentication.getName();
            return Optional.of(email);
        };
    }
}

