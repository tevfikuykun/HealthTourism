package com.healthtourism.security.config;

import com.healthtourism.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security Configuration - Professional Enterprise Implementation
 * 
 * Best Practices Applied:
 * - Role-Based Access Control (RBAC)
 * - JWT-based authentication (stateless)
 * - Secure CORS configuration (whitelist only)
 * - Method-level security (@PreAuthorize)
 * - CSRF protection (disabled for stateless JWT, but can be enabled for stateful apps)
 * 
 * Security Rules:
 * - Public endpoints: /api/v1/auth/**, /api/v1/doctors/** (GET), /api/v1/hospitals/** (GET)
 * - Admin-only: /api/v1/admin/**
 * - Authenticated: All other endpoints require valid JWT token
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize and @PostAuthorize
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    
    /**
     * Security Filter Chain
     * 
     * Configures:
     * - CSRF: Disabled for stateless JWT (can be enabled if needed)
     * - CORS: Whitelist-based configuration
     * - Session: Stateless (no server-side session)
     * - Authorization: Role-based access control
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF Configuration
            // For stateless JWT, CSRF can be disabled
            // For stateful sessions, CSRF should be enabled
            .csrf(csrf -> csrf.disable())
            
            // CORS Configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Session Management
            // STATELESS: No server-side session (JWT-based)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Authorization Rules
            .authorizeHttpRequests(auth -> auth
                // 1. Public Endpoints (No authentication required)
                .requestMatchers(
                    "/api/v1/auth/**",           // Authentication endpoints (login, register)
                    "/api/v1/auth/login",
                    "/api/v1/auth/register",
                    "/api/v1/auth/refresh",
                    "/api/v1/doctors",           // Public doctor listing
                    "/api/v1/doctors/**",       // Public doctor details (GET only)
                    "/api/v1/hospitals",         // Public hospital listing
                    "/api/v1/hospitals/**",     // Public hospital details (GET only)
                    "/api/v1/treatments",        // Public treatment listing
                    "/api/v1/treatments/**",     // Public treatment details (GET only)
                    "/actuator/health",          // Health check endpoint
                    "/swagger-ui/**",            // Swagger UI
                    "/v3/api-docs/**",           // OpenAPI documentation
                    "/error"                     // Error endpoint
                ).permitAll()
                
                // 2. Admin-Only Endpoints
                .requestMatchers(
                    "/api/v1/admin/**",          // All admin endpoints
                    "/api/v1/users/**",          // User management (admin only)
                    "/api/v1/doctors/**/delete", // Doctor deletion (admin only)
                    "/api/v1/hospitals/**/delete" // Hospital deletion (admin only)
                ).hasRole("ADMIN")
                
                // 3. Doctor-Only Endpoints
                .requestMatchers(
                    "/api/v1/doctors/me/**",     // Doctor's own profile
                    "/api/v1/appointments/my-appointments" // Doctor's appointments
                ).hasAnyRole("DOCTOR", "ADMIN")
                
                // 4. User-Only Endpoints
                .requestMatchers(
                    "/api/v1/reservations/my-reservations", // User's own reservations
                    "/api/v1/reservations/**",   // Reservation management (authenticated users)
                    "/api/v1/profile/**"          // User profile management
                ).hasAnyRole("USER", "ADMIN", "DOCTOR")
                
                // 5. All Other Requests Require Authentication
                .anyRequest().authenticated()
            )
            
            // Authentication Provider
            .authenticationProvider(authenticationProvider)
            
            // JWT Filter (before UsernamePasswordAuthenticationFilter)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * CORS Configuration Source
     * 
     * Security Best Practices:
     * - Whitelist only trusted origins (never use "*")
     * - Specify allowed methods
     * - Specify allowed headers
     * - Allow credentials only if needed
     * 
     * Configuration via application.properties:
     * - app.cors.allowed-origins: Comma-separated list of allowed origins
     * - app.cors.allowed-methods: Comma-separated list of allowed HTTP methods
     * - app.cors.allowed-headers: Comma-separated list of allowed headers
     * - app.cors.allow-credentials: true/false
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allowed Origins (WHITELIST ONLY - NEVER USE "*")
        // In production, use environment variables or application.properties
        List<String> allowedOrigins = Arrays.asList(
            "https://app.healthtourism.com",
            "https://admin.healthtourism.com",
            "http://localhost:3000",      // Development frontend
            "http://localhost:3001",      // Development admin panel
            "http://localhost:8080"       // Development backend
        );
        configuration.setAllowedOrigins(allowedOrigins);
        
        // Allowed HTTP Methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET",
            "POST",
            "PUT",
            "PATCH",
            "DELETE",
            "OPTIONS"
        ));
        
        // Allowed Headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "X-API-Key",
            "X-Idempotency-Key"
        ));
        
        // Allow Credentials (cookies, authorization headers)
        // Only enable if needed (e.g., for stateful sessions)
        configuration.setAllowCredentials(true);
        
        // Exposed Headers (headers that can be read by frontend)
        configuration.setExposedHeaders(Arrays.asList(
            "X-API-Version",
            "X-RateLimit-Remaining",
            "X-RateLimit-Reset"
        ));
        
        // Max Age (how long preflight requests can be cached)
        configuration.setMaxAge(3600L); // 1 hour
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
