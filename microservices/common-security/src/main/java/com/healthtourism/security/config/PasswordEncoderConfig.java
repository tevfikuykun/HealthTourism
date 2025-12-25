package com.healthtourism.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password Encoder Configuration
 * 
 * Uses BCrypt for password hashing:
 * - Salt is automatically generated and included in hash
 * - Strength: 10 (default, can be increased for more security)
 * - One-way hashing (cannot be reversed)
 * 
 * Security Best Practices:
 * - Never store passwords in plain text
 * - Use BCrypt or Argon2 for password hashing
 * - BCrypt automatically handles salt generation
 */
@Configuration
public class PasswordEncoderConfig {
    
    /**
     * BCrypt Password Encoder Bean
     * 
     * Strength: 10 (2^10 = 1024 rounds)
     * Higher strength = more secure but slower
     * Recommended: 10-12 for production
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}

