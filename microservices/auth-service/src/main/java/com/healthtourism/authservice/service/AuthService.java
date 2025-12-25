package com.healthtourism.authservice.service;

import com.healthtourism.authservice.dto.AuthResponse;
import com.healthtourism.authservice.dto.LoginRequest;
import com.healthtourism.authservice.dto.RegisterRequest;
import com.healthtourism.authservice.entity.PasswordResetToken;
import com.healthtourism.authservice.entity.RefreshToken;
import com.healthtourism.authservice.entity.User;
import com.healthtourism.authservice.event.OnPasswordResetRequestEvent;
import com.healthtourism.authservice.event.OnUserRegistrationEvent;
import com.healthtourism.authservice.exception.*;
import com.healthtourism.authservice.repository.PasswordResetTokenRepository;
import com.healthtourism.authservice.repository.RefreshTokenRepository;
import com.healthtourism.authservice.repository.UserRepository;
import com.healthtourism.authservice.util.ClientInfoExtractor;
import com.healthtourism.authservice.util.JwtUtil;
import com.healthtourism.authservice.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Authentication Service - Professional Enterprise Implementation
 * 
 * Best Practices Applied:
 * - Async email sending (event-based)
 * - Rate limiting for security
 * - Custom exception hierarchy with error codes
 * - Password strength validation
 * - IP and User-Agent tracking for audit
 * - Production-ready email verification (default: false)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final RateLimitService rateLimitService;
    private final ClientInfoExtractor clientInfoExtractor;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * Register a new user
     * 
     * Security Best Practices:
     * - Password is hashed with BCrypt before storage
     * - Email verification required (setEmailVerified = false)
     * - Async email sending (non-blocking)
     * 
     * @param request Registration request
     * @return AuthResponse with JWT tokens
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        
        // Validate password strength
        passwordValidator.validate(request.getPassword());
        
        // Create user entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setCountry(request.getCountry());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        user.setIsActive(true);
        
        // Production: Email verification required (default: false)
        user.setEmailVerified(false); // Changed from true - production ready
        
        // Generate email verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusDays(1));
        
        User savedUser = userRepository.save(user);
        
        // ASYNC: Publish event for email sending (non-blocking)
        eventPublisher.publishEvent(new OnUserRegistrationEvent(savedUser, verificationToken));
        
        // Generate JWT tokens
        String accessToken = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId(), savedUser.getRole());
        String refreshToken = generateRefreshToken(savedUser.getId());
        
        log.info("User registered successfully: {}", savedUser.getEmail());
        
        return new AuthResponse(
            accessToken,
            refreshToken,
            "Bearer",
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getRole(),
            jwtUtil.extractExpiration(accessToken).getTime() - System.currentTimeMillis()
        );
    }
    
    /**
     * Authenticate user and generate JWT token
     * 
     * Security Best Practices:
     * - Rate limiting for failed attempts
     * - Password is never logged
     * - IP and User-Agent tracking
     * 
     * @param request Login request
     * @return AuthResponse with JWT tokens
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String clientIp = clientInfoExtractor.extractClientIp();
        log.info("Login attempt for email: {} from IP: {}", request.getEmail(), clientIp);
        
        // Rate limiting check
        if (!rateLimitService.isLoginAttemptAllowed(clientIp)) {
            log.warn("Login attempt blocked due to rate limit: {} from IP: {}", request.getEmail(), clientIp);
            throw new AuthException(
                AuthErrorCode.ACCOUNT_LOCKED,
                "Too many login attempts. Please try again later."
            );
        }
        
        // Find user
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> {
                rateLimitService.recordFailedLoginAttempt(clientIp);
                return new InvalidCredentialsException();
            });
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            rateLimitService.recordFailedLoginAttempt(clientIp);
            throw new InvalidCredentialsException();
        }
        
        // Check if account is active
        if (!user.getIsActive()) {
            throw new AuthException(AuthErrorCode.ACCOUNT_INACTIVE);
        }
        
        // Check if email is verified (production requirement)
        if (!user.getEmailVerified()) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }
        
        // Clear failed login attempts on successful login
        rateLimitService.clearLoginAttempts(clientIp);
        
        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate JWT tokens
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole());
        String refreshToken = generateRefreshToken(user.getId());
        
        log.info("User logged in successfully: {}", user.getEmail());
        
        return new AuthResponse(
            accessToken,
            refreshToken,
            "Bearer",
            user.getId(),
            user.getEmail(),
            user.getRole(),
            jwtUtil.extractExpiration(accessToken).getTime() - System.currentTimeMillis()
        );
    }
    
    /**
     * Refresh access token using refresh token
     * 
     * @param refreshToken Refresh token
     * @return AuthResponse with new access token
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new AuthException(AuthErrorCode.REFRESH_TOKEN_INVALID));
        
        if (token.getIsRevoked() || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        
        User user = userRepository.findById(token.getUserId())
            .orElseThrow(() -> new AuthException(AuthErrorCode.INTERNAL_ERROR, "User not found"));
        
        String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole());
        String newRefreshToken = generateRefreshToken(user.getId());
        
        // Revoke old refresh token
        token.setIsRevoked(true);
        refreshTokenRepository.save(token);
        
        return new AuthResponse(
            newAccessToken,
            newRefreshToken,
            "Bearer",
            user.getId(),
            user.getEmail(),
            user.getRole(),
            jwtUtil.extractExpiration(newAccessToken).getTime() - System.currentTimeMillis()
        );
    }
    
    /**
     * Logout user (revoke refresh token)
     * 
     * @param refreshToken Refresh token to revoke
     */
    public void logout(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);
        if (token.isPresent()) {
            token.get().setIsRevoked(true);
            refreshTokenRepository.save(token.get());
            log.info("User logged out successfully");
        }
    }
    
    /**
     * Request password reset
     * 
     * Security Best Practices:
     * - Rate limiting (3 requests per hour)
     * - IP and User-Agent tracking
     * - Async email sending
     * 
     * @param email User email
     */
    public void requestPasswordReset(String email) {
        String clientIp = clientInfoExtractor.extractClientIp();
        String userAgent = clientInfoExtractor.extractUserAgent();
        
        log.info("Password reset request for email: {} from IP: {}", email, clientIp);
        
        // Rate limiting check
        if (!rateLimitService.isPasswordResetAllowed(email)) {
            int remaining = rateLimitService.getRemainingPasswordResetRequests(email);
            log.warn("Password reset request blocked due to rate limit: {} from IP: {}", email, clientIp);
            throw new AuthException(
                AuthErrorCode.PASSWORD_RESET_REQUEST_LIMIT_EXCEEDED,
                String.format("Too many password reset requests. Please try again in %d hour(s). Remaining requests: %d", 
                    1, remaining)
            );
        }
        
        // Find user
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                // Don't reveal if email exists (security best practice)
                log.warn("Password reset request for non-existent email: {}", email);
                return new AuthException(
                    AuthErrorCode.INVALID_CREDENTIALS,
                    "If the email exists, a password reset link has been sent"
                );
            });
        
        // Delete old reset tokens
        passwordResetTokenRepository.deleteByUserId(user.getId());
        
        // Generate new reset token
        String resetToken = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
            .token(resetToken)
            .userId(user.getId())
            .expiryDate(LocalDateTime.now().plusHours(1))
            .isUsed(false)
            .clientIp(clientIp) // Track IP for audit
            .userAgent(userAgent) // Track User-Agent for audit
            .build();
        passwordResetTokenRepository.save(passwordResetToken);
        
        // Record rate limit
        rateLimitService.recordPasswordResetRequest(email);
        
        // ASYNC: Publish event for email sending (non-blocking)
        eventPublisher.publishEvent(new OnPasswordResetRequestEvent(
            user.getId(), email, resetToken, clientIp, userAgent
        ));
        
        log.info("Password reset token generated for email: {}", email);
    }
    
    /**
     * Reset password using reset token
     * 
     * Security Best Practices:
     * - Password strength validation
     * - Token validation (expired, used)
     * - IP and User-Agent tracking
     * 
     * @param token Reset token
     * @param newPassword New password
     */
    public void resetPassword(String token, String newPassword) {
        log.info("Password reset attempt with token");
        
        // Find reset token
        PasswordResetToken resetToken = passwordResetTokenRepository
            .findByTokenAndIsUsedFalse(token)
            .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_RESET_TOKEN));
        
        // Check if token is expired
        if (resetToken.isExpired()) {
            throw new AuthException(AuthErrorCode.RESET_TOKEN_ALREADY_USED);
        }
        
        // Validate new password strength
        passwordValidator.validate(newPassword);
        
        // Find user
        User user = userRepository.findById(resetToken.getUserId())
            .orElseThrow(() -> new AuthException(AuthErrorCode.INTERNAL_ERROR, "User not found"));
        
        // Check if new password is different from current password
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new AuthException(AuthErrorCode.SAME_PASSWORD);
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Mark token as used
        resetToken.markAsUsed();
        passwordResetTokenRepository.save(resetToken);
        
        log.info("Password reset successful for user: {}", user.getEmail());
    }
    
    /**
     * Verify email with verification token
     * 
     * @param token Verification token
     */
    public void verifyEmail(String token) {
        log.info("Email verification attempt with token");
        
        User user = userRepository.findByVerificationToken(token)
            .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_VERIFICATION_TOKEN));
        
        if (user.getEmailVerified()) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_VERIFIED);
        }
        
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new AuthException(AuthErrorCode.VERIFICATION_TOKEN_EXPIRED);
        }
        
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        
        log.info("Email verified successfully for user: {}", user.getEmail());
    }
    
    /**
     * Resend verification email
     * 
     * @param email User email
     */
    public void resendVerificationEmail(String email) {
        log.info("Resend verification email request for: {}", email);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_CREDENTIALS));
        
        if (user.getEmailVerified()) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_VERIFIED);
        }
        
        // Generate new verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusDays(1));
        userRepository.save(user);
        
        // ASYNC: Send verification email
        eventPublisher.publishEvent(new OnUserRegistrationEvent(user, verificationToken));
        
        log.info("Verification email resent for: {}", email);
    }
    
    /**
     * Generate refresh token
     * 
     * @param userId User ID
     * @return Refresh token string
     */
    private String generateRefreshToken(Long userId) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(30));
        refreshToken.setIsRevoked(false);
        refreshTokenRepository.save(refreshToken);
        return token;
    }
    
    /**
     * Validate token
     * 
     * @param token JWT token
     * @return true if valid
     */
    @Transactional(readOnly = true)
    public Boolean validateToken(String token) {
        try {
            String email = jwtUtil.extractEmail(token);
            return jwtUtil.validateToken(token, email);
        } catch (Exception e) {
            return false;
        }
    }
}
