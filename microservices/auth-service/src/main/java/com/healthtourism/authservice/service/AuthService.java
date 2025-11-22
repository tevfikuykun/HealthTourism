package com.healthtourism.authservice.service;

import com.healthtourism.authservice.dto.AuthResponse;
import com.healthtourism.authservice.dto.LoginRequest;
import com.healthtourism.authservice.dto.RegisterRequest;
import com.healthtourism.authservice.entity.RefreshToken;
import com.healthtourism.authservice.entity.User;
import com.healthtourism.authservice.repository.RefreshTokenRepository;
import com.healthtourism.authservice.repository.UserRepository;
import com.healthtourism.authservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setCountry(request.getCountry());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        user.setIsActive(true);
        
        user = userRepository.save(user);
        
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole());
        String refreshToken = generateRefreshToken(user.getId());
        
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
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        if (!user.getIsActive()) {
            throw new RuntimeException("User account is inactive");
        }
        
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole());
        String refreshToken = generateRefreshToken(user.getId());
        
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
    
    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        
        if (token.getIsRevoked() || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired or revoked");
        }
        
        User user = userRepository.findById(token.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole());
        String newRefreshToken = generateRefreshToken(user.getId());
        
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
    
    public void logout(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);
        if (token.isPresent()) {
            token.get().setIsRevoked(true);
            refreshTokenRepository.save(token.get());
        }
    }
    
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
    
    public Boolean validateToken(String token) {
        try {
            String email = jwtUtil.extractEmail(token);
            return jwtUtil.validateToken(token, email);
        } catch (Exception e) {
            return false;
        }
    }
}

