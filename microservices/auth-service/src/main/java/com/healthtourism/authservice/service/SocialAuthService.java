package com.healthtourism.authservice.service;

import com.healthtourism.authservice.dto.AuthResponse;
import com.healthtourism.authservice.entity.RefreshToken;
import com.healthtourism.authservice.entity.User;
import com.healthtourism.authservice.repository.RefreshTokenRepository;
import com.healthtourism.authservice.repository.UserRepository;
import com.healthtourism.authservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SocialAuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
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
    
    @Transactional
    public AuthResponse authenticateWithGoogle(String googleId, String email, String firstName, String lastName, String profilePicture) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Google ID'yi kaydet
            user.setGoogleId(googleId);
            if (profilePicture != null) {
                user.setProfilePicture(profilePicture);
            }
            user.setLastLogin(LocalDateTime.now());
        } else {
            // Yeni kullanıcı oluştur
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setGoogleId(googleId);
            user.setProfilePicture(profilePicture);
            user.setRole("USER");
            user.setIsActive(true);
            user.setEmailVerified(true); // Google ile gelen email zaten doğrulanmış
            user.setLastLogin(LocalDateTime.now());
        }
        
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
    
    @Transactional
    public AuthResponse authenticateWithFacebook(String facebookId, String email, String firstName, String lastName, String profilePicture) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setFacebookId(facebookId);
            if (profilePicture != null) {
                user.setProfilePicture(profilePicture);
            }
            user.setLastLogin(LocalDateTime.now());
        } else {
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setFacebookId(facebookId);
            user.setProfilePicture(profilePicture);
            user.setRole("USER");
            user.setIsActive(true);
            user.setEmailVerified(true);
            user.setLastLogin(LocalDateTime.now());
        }
        
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
}

