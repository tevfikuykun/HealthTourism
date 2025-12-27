package com.example.HealthTourism.config;

import com.bucket4j.Bandwidth;
import com.bucket4j.Bucket;
import com.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiting Filter
 * IP bazlı rate limiting - Bot saldırılarına ve kötü niyetli yoğun isteklere karşı koruma
 * 
 * Özellikler:
 * - IP bazlı rate limiting (dakikada 60 istek)
 * - Token bazlı rate limiting (daha yüksek limit)
 * - 429 Too Many Requests response
 * - Rate limit bilgisi header'ları (X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset)
 * 
 * Kullanım:
 * SecurityConfig'de JWT filter'dan önce çalışır
 */
@Slf4j
@Component
@Order(1) // JWT filter'dan önce çalışmalı
public class RateLimitingFilter extends OncePerRequestFilter {
    
    @Value("${rate.limit.enabled:true}")
    private boolean rateLimitEnabled;
    
    @Value("${rate.limit.requests-per-minute:60}")
    private int requestsPerMinute;
    
    @Value("${rate.limit.authenticated-requests-per-minute:120}")
    private int authenticatedRequestsPerMinute;
    
    // IP bazlı bucket'lar
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Rate limiting devre dışıysa
        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Public endpoint'ler için rate limiting (actuator, swagger, etc.)
        String path = request.getRequestURI();
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Client identifier (IP veya User ID)
        String clientId = getClientIdentifier(request);
        
        // Token var mı? (Authenticated user)
        boolean isAuthenticated = request.getHeader("Authorization") != null;
        int limit = isAuthenticated ? authenticatedRequestsPerMinute : requestsPerMinute;
        
        // Bucket al veya oluştur
        Bucket bucket = cache.computeIfAbsent(clientId, k -> createBucket(limit));
        
        // Rate limit kontrolü
        if (bucket.tryConsume(1)) {
            // İstek izin verildi
            response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(bucket.getAvailableTokens()));
            response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + 60));
            
            filterChain.doFilter(request, response);
        } else {
            // Rate limit aşıldı
            log.warn("Rate limit exceeded for client: {} ({} requests/min)", clientId, limit);
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("Retry-After", "60"); // 60 saniye sonra tekrar dene
            
            response.getWriter().write("{\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Please try again later.\"}");
        }
    }
    
    /**
     * Client Identifier
     * IP adresi veya user ID (authenticated ise)
     */
    private String getClientIdentifier(HttpServletRequest request) {
        // X-Forwarded-For header'ından IP al (reverse proxy arkasındaysa)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        // Remote address'ten IP al
        return request.getRemoteAddr();
    }
    
    /**
     * Bucket Oluştur
     * Token bucket algoritması ile rate limiting
     */
    private Bucket createBucket(int limit) {
        Bandwidth limitBandwidth = Bandwidth.classic(
                limit,
                Refill.intervally(limit, Duration.ofMinutes(1))
        );
        return Bucket.builder()
                .addLimit(limitBandwidth)
                .build();
    }
    
    /**
     * Public Endpoint Kontrolü
     * Rate limiting uygulanmayacak endpoint'ler
     */
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/api-docs/") ||
               path.startsWith("/v3/api-docs/") ||
               path.startsWith("/error");
    }
}

