package com.healthtourism.authservice.service;

import com.healthtourism.authservice.dto.AuthResponse;
import com.healthtourism.authservice.dto.LoginRequest;
import com.healthtourism.authservice.dto.RegisterRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ResilientAuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(ResilientAuthService.class);
    
    @Autowired
    private AuthService authService;
    
    @CircuitBreaker(name = "authService", fallbackMethod = "registerFallback")
    @Retry(name = "authService")
    public AuthResponse register(RegisterRequest request) {
        logger.info("Attempting user registration for email: {}", request.getEmail());
        return authService.register(request);
    }
    
    @CircuitBreaker(name = "authService", fallbackMethod = "loginFallback")
    @Retry(name = "authService")
    public AuthResponse login(LoginRequest request) {
        logger.info("Attempting login for email: {}", request.getEmail());
        return authService.login(request);
    }
    
    // Fallback methods
    private AuthResponse registerFallback(RegisterRequest request, Exception ex) {
        logger.error("Registration failed, using fallback: {}", ex.getMessage());
        throw new RuntimeException("Registration service is temporarily unavailable. Please try again later.");
    }
    
    private AuthResponse loginFallback(LoginRequest request, Exception ex) {
        logger.error("Login failed, using fallback: {}", ex.getMessage());
        throw new RuntimeException("Login service is temporarily unavailable. Please try again later.");
    }
}

