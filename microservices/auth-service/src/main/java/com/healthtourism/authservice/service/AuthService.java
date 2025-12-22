package com.healthtourism.authservice.service;

import com.healthtourism.authservice.dto.AuthResponse;
import com.healthtourism.authservice.dto.LoginRequest;
import com.healthtourism.authservice.dto.RegisterRequest;
import com.healthtourism.authservice.entity.PasswordResetToken;
import com.healthtourism.authservice.entity.RefreshToken;
import com.healthtourism.authservice.entity.User;
import com.healthtourism.authservice.repository.PasswordResetTokenRepository;
import com.healthtourism.authservice.repository.RefreshTokenRepository;
import com.healthtourism.authservice.repository.UserRepository;
import com.healthtourism.authservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;

    @Value("${spring.mail.username:noreply@healthtourism.com}")
    private String fromEmail;
    
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
        // Auto-verify email for development - set to true
        // TODO: Set to false and implement email verification flow in production
        user.setEmailVerified(true);
        
        // Email verification token oluştur
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusDays(1));
        
        user = userRepository.save(user);
        
        // Email verification gönder
        sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationToken);
        
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

        // Email verification check - temporarily disabled for development
        // TODO: Re-enable email verification in production
        // if (user.getEmailVerified() == null || !user.getEmailVerified()) {
        //     throw new RuntimeException("Please verify your email before logging in");
        // }
        
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

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
    }

    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getEmailVerified() != null && user.getEmailVerified()) {
            throw new RuntimeException("Email already verified");
        }

        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusDays(1));
        userRepository.save(user);

        sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationToken);
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Eski token'ları temizle
        passwordResetTokenRepository.deleteByUserId(user.getId());

        // Yeni token oluştur
        String resetToken = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(resetToken);
        passwordResetToken.setUserId(user.getId());
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        passwordResetToken.setIsUsed(false);
        passwordResetToken.setCreatedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(passwordResetToken);

        // Email gönder
        sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetToken);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository
            .findByTokenAndIsUsedFalse(token)
            .orElseThrow(() -> new RuntimeException("Invalid or used reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        User user = userRepository.findById(resetToken.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setIsUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    private void sendVerificationEmail(String to, String name, String token) {
        if (mailSender == null) {
            System.out.println("Email verification token for " + to + ": " + baseUrl + "/verify-email?token=" + token);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Email Doğrulama - Health Tourism");
            message.setText("Merhaba " + name + ",\n\n" +
                "Hesabınızı doğrulamak için aşağıdaki linke tıklayın:\n\n" +
                baseUrl + "/verify-email?token=" + token + "\n\n" +
                "Bu link 24 saat geçerlidir.\n\n" +
                "Saygılarımla,\nHealth Tourism Ekibi");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
    }

    private void sendPasswordResetEmail(String to, String name, String token) {
        if (mailSender == null) {
            System.out.println("Password reset token for " + to + ": " + baseUrl + "/reset-password?token=" + token);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Şifre Sıfırlama - Health Tourism");
            message.setText("Merhaba " + name + ",\n\n" +
                "Şifrenizi sıfırlamak için aşağıdaki linke tıklayın:\n\n" +
                baseUrl + "/reset-password?token=" + token + "\n\n" +
                "Bu link 1 saat geçerlidir.\n\n" +
                "Eğer bu işlemi siz yapmadıysanız, bu e-postayı görmezden gelebilirsiniz.\n\n" +
                "Saygılarımla,\nHealth Tourism Ekibi");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
        }
    }
}

