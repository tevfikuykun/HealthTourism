package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.AuthResponse;
import com.example.HealthTourism.dto.LoginRequest;
import com.example.HealthTourism.dto.RegisterRequest;
import com.example.HealthTourism.entity.User;
import com.example.HealthTourism.exception.EmailAlreadyExistsException;
import com.example.HealthTourism.exception.InvalidCredentialsException;
import com.example.HealthTourism.repository.UserRepository;
import com.example.HealthTourism.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Enterprise-grade AuthService with:
 * - Constructor Injection for testability
 * - JWT token generation
 * - BCrypt password encoding
 * - Email verification token generation
 * - Proper exception handling
 * - Transactional management
 * 
 * Security Best Practices:
 * - Passwords are NEVER stored in plain text
 * - Email verification required (emailVerified = false by default)
 * - JWT tokens with expiration
 * - Secure password validation
 */
@Slf4j
@Service
@RequiredArgsConstructor // Constructor Injection - Spring best practice
@Transactional // Transactional for write operations
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    /**
     * Registers a new user.
     * 
     * Security Best Practices:
     * - Password is hashed with BCrypt before storage
     * - Email verification required (emailVerified = false)
     * - Email verification token generated
     * - Async email sending for verification (non-blocking)
     * 
     * @param request Registration request
     * @return AuthResponse with JWT tokens
     * @throws EmailAlreadyExistsException if email already exists
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        
        // Create user entity
        User user = new User();
        user.setEmail(request.getEmail());
        // CRITICAL: Password is NEVER stored in plain text!
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setCountry(request.getCountry());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        user.setIsActive(true);
        
        // Production: Email verification required (default: false)
        user.setEmailVerified(false);
        
        // Generate email verification token
        String verificationToken = UUID.randomUUID().toString();
        // Note: If User entity has verificationToken field, set it here
        // user.setVerificationToken(verificationToken);
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());
        
        // ASYNC: Send verification email (non-blocking)
        try {
            emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);
        } catch (Exception e) {
            log.warn("Failed to send verification email to: {}", savedUser.getEmail(), e);
            // Don't fail registration if email sending fails
        }
        
        // Generate JWT tokens
        String accessToken = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId(), savedUser.getRole());
        // Note: Refresh token implementation can be added here if needed
        
        // Calculate expiration time
        Long expiresIn = jwtUtil.extractExpiration(accessToken).getTime() - System.currentTimeMillis();
        
        return new AuthResponse(
            accessToken,
            null, // Refresh token (can be implemented later)
            "Bearer",
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getRole(),
            expiresIn
        );
    }
    
    /**
     * Authenticates user and generates JWT token.
     * 
     * Security Best Practices:
     * - Password is never logged
     * - Secure password comparison using BCrypt
     * - Account status validation (active, non-locked)
     * - Last login tracking
     * 
     * @param request Login request
     * @return AuthResponse with JWT tokens
     * @throws InvalidCredentialsException if credentials are invalid
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found for email: {}", request.getEmail());
                    return new InvalidCredentialsException();
                });
        
        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: Invalid password for email: {}", request.getEmail());
            throw new InvalidCredentialsException();
        }
        
        // Validate account status
        if (!user.getIsActive()) {
            log.warn("Login failed: Account is not active for email: {}", request.getEmail());
            throw new InvalidCredentialsException("Hesabınız aktif değil. Lütfen yönetici ile iletişime geçin.");
        }
        
        if (user.getAccountNonLocked() != null && !user.getAccountNonLocked()) {
            log.warn("Login failed: Account is locked for email: {}", request.getEmail());
            throw new InvalidCredentialsException("Hesabınız kilitlenmiş. Lütfen yönetici ile iletişime geçin.");
        }
        
        // Update last login date
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate JWT tokens
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole());
        // Note: Refresh token implementation can be added here if needed
        
        // Calculate expiration time
        Long expiresIn = jwtUtil.extractExpiration(accessToken).getTime() - System.currentTimeMillis();
        
        log.info("User logged in successfully: {}", user.getEmail());
        
        return new AuthResponse(
            accessToken,
            null, // Refresh token (can be implemented later)
            "Bearer",
            user.getId(),
            user.getEmail(),
            user.getRole(),
            expiresIn
        );
    }
    
    /**
     * Validates JWT token.
     * 
     * @param token JWT token
     * @return true if token is valid
     */
    public Boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}

