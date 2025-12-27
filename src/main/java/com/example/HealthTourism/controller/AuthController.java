package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.AuthResponse;
import com.example.HealthTourism.dto.LoginRequest;
import com.example.HealthTourism.dto.RegisterRequest;
import com.example.HealthTourism.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles login and registration endpoints
 * 
 * Error handling is delegated to GlobalExceptionHandler:
 * - EmailAlreadyExistsException -> 409 CONFLICT
 * - InvalidCredentialsException -> 401 UNAUTHORIZED
 * - MethodArgumentNotValidException -> 400 BAD_REQUEST (validation errors)
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Register a new user
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Login user
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Validate JWT token
     * POST /api/v1/auth/validate
     * 
     * Note: In production, JWT validation is typically handled by Security Filter Chain,
     * but this endpoint is useful for client-side validation or microservice communication.
     */
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        Boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}

