package com.healthtourism.security.config;

import com.healthtourism.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Authentication Provider Configuration
 * 
 * Configures Spring Security authentication provider:
 * - UserDetailsService: Loads user information
 * - PasswordEncoder: Validates passwords
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationProviderConfig {
    
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    
    /**
     * Authentication Provider Bean
     * 
     * Uses DaoAuthenticationProvider which:
     * - Loads user by username
     * - Validates password using PasswordEncoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}

