package com.example.HealthTourism.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security Configuration
 * JWT-based stateless authentication with role-based access control (RBAC)
 * 
 * Security Features:
 * - JWT token validation via JwtAuthenticationFilter
 * - Stateless session management (no server-side sessions)
 * - Role-based access control (RBAC)
 * - CORS configuration for cross-origin requests
 * 
 * Security Notes:
 * - jwt.secret must be at least 256-bit (32 characters) for HS256 algorithm
 * - In production, jwt.secret should be managed via environment variables or secure vault
 * - Never hardcode secrets in source code
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitingFilter rateLimitingFilter;
    
    /**
     * Password Encoder Bean for BCrypt password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Security Filter Chain
     * Configures authentication and authorization rules
     * 
     * RBAC (Role-Based Access Control):
     * - PUBLIC: Authentication endpoints (/api/v1/auth/**)
     * - PUBLIC: GET requests to packages (public browsing)
     * - ADMIN: Admin endpoints (/api/v1/admin/**) - requires ADMIN role
     * - AUTHENTICATED: All other endpoints require valid JWT token
     * 
     * Session Management: STATELESS (JWT tokens are stateless, no server-side sessions)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Session yönetimi Stateless (durumsuz) olmalı çünkü JWT kullanıyoruz
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - authentication not required
                .requestMatchers("/actuator/**", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll() // Login/Register serbest
                // Public GET endpoints for browsing (can be restricted later if needed)
                .requestMatchers(HttpMethod.GET, "/api/v1/packages/**").permitAll() // Paketleri herkes görsün
                .requestMatchers(HttpMethod.GET, "/api/v1/hospitals/**").permitAll() // Hastaneleri herkes görsün
                .requestMatchers(HttpMethod.GET, "/api/v1/doctors/**").permitAll() // Doktorları herkes görsün
                .requestMatchers(HttpMethod.GET, "/api/v1/accommodations/**").permitAll() // Konaklamaları herkes görsün
                .requestMatchers(HttpMethod.GET, "/api/v1/flights/**").permitAll() // Uçuşları herkes görsün
                .requestMatchers(HttpMethod.GET, "/api/v1/transfers/**").permitAll() // Transferleri herkes görsün
                .requestMatchers(HttpMethod.GET, "/api/v1/car-rentals/**").permitAll() // Araç kiralama herkes görsün
                .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll() // Yorumları herkes görsün
                // Admin endpoints - requires ADMIN role
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // Admin paneli kilitli
                // All other requests require authentication
                .anyRequest().authenticated() // Geri kalan her şey için login şart!
            )
            // Rate limiting filter'ı en başa ekle (tüm istekleri kontrol etmek için)
            .addFilterBefore(rateLimitingFilter, JwtAuthenticationFilter.class)
            // Kendi yazdığımız JWT filtresini UsernamePasswordAuthenticationFilter'dan önceye ekle
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * CORS Configuration
     * Allows cross-origin requests from frontend applications
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Production'da spesifik domain'ler belirtilmeli
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // Frontend'in token'ı okuması için
        configuration.setAllowCredentials(false); // JWT kullanıldığı için credentials gerekmez
        configuration.setMaxAge(3600L); // Preflight cache duration (1 hour)
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

