package com.healthtourism.authservice.service;

import com.healthtourism.authservice.dto.AuthResponse;
import com.healthtourism.authservice.dto.LoginRequest;
import com.healthtourism.authservice.dto.RegisterRequest;
import com.healthtourism.authservice.entity.User;
import com.healthtourism.authservice.repository.RefreshTokenRepository;
import com.healthtourism.authservice.repository.UserRepository;
import com.healthtourism.authservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive Unit Tests for AuthService
 * Designed for high mutation test coverage with PITest
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceMutationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole("USER");
        testUser.setIsActive(true);
        testUser.setEmailVerified(true);

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("New");
        registerRequest.setLastName("User");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register user successfully with valid data")
        void shouldRegisterUserSuccessfully() {
            // Arrange
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User user = inv.getArgument(0);
                user.setId(1L);
                return user;
            });
            when(jwtUtil.generateToken(anyString(), anyLong(), anyString())).thenReturn("jwt-token");
            when(jwtUtil.extractExpiration(anyString())).thenReturn(new Date(System.currentTimeMillis() + 86400000));
            when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            AuthResponse response = authService.register(registerRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("jwt-token");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            // Arrange
            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already exists");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should set default role when not provided")
        void shouldSetDefaultRoleWhenNotProvided() {
            // Arrange
            registerRequest.setRole(null);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User user = inv.getArgument(0);
                user.setId(1L);
                assertThat(user.getRole()).isEqualTo("USER");
                return user;
            });
            when(jwtUtil.generateToken(anyString(), anyLong(), anyString())).thenReturn("jwt-token");
            when(jwtUtil.extractExpiration(anyString())).thenReturn(new Date(System.currentTimeMillis() + 86400000));
            when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            authService.register(registerRequest);

            // Assert
            verify(userRepository).save(argThat(user -> "USER".equals(user.getRole())));
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void shouldLoginSuccessfully() {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(jwtUtil.generateToken(anyString(), anyLong(), anyString())).thenReturn("jwt-token");
            when(jwtUtil.extractExpiration(anyString())).thenReturn(new Date(System.currentTimeMillis() + 86400000));
            when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            AuthResponse response = authService.login(loginRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("jwt-token");
            assertThat(response.getUserId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception for invalid email")
        void shouldThrowExceptionForInvalidEmail() {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid email or password");
        }

        @Test
        @DisplayName("Should throw exception for invalid password")
        void shouldThrowExceptionForInvalidPassword() {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid email or password");
        }

        @Test
        @DisplayName("Should throw exception for inactive user")
        void shouldThrowExceptionForInactiveUser() {
            // Arrange
            testUser.setIsActive(false);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User account is inactive");
        }

        @Test
        @DisplayName("Should throw exception for unverified email")
        void shouldThrowExceptionForUnverifiedEmail() {
            // Arrange
            testUser.setEmailVerified(false);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Please verify your email before logging in");
        }

        @Test
        @DisplayName("Should update last login time on successful login")
        void shouldUpdateLastLoginTime() {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
            when(jwtUtil.generateToken(anyString(), anyLong(), anyString())).thenReturn("jwt-token");
            when(jwtUtil.extractExpiration(anyString())).thenReturn(new Date(System.currentTimeMillis() + 86400000));
            when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            authService.login(loginRequest);

            // Assert
            verify(userRepository).save(argThat(user -> user.getLastLogin() != null));
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("Should return true for valid token")
        void shouldReturnTrueForValidToken() {
            // Arrange
            when(jwtUtil.extractEmail(anyString())).thenReturn("test@example.com");
            when(jwtUtil.validateToken(anyString(), anyString())).thenReturn(true);

            // Act
            Boolean result = authService.validateToken("valid-token");

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false for invalid token")
        void shouldReturnFalseForInvalidToken() {
            // Arrange
            when(jwtUtil.extractEmail(anyString())).thenThrow(new RuntimeException("Invalid token"));

            // Act
            Boolean result = authService.validateToken("invalid-token");

            // Assert
            assertThat(result).isFalse();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should handle null and empty tokens")
        void shouldHandleNullAndEmptyTokens(String token) {
            // Arrange
            when(jwtUtil.extractEmail(any())).thenThrow(new RuntimeException("Invalid token"));

            // Act
            Boolean result = authService.validateToken(token);

            // Assert
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Boundary Tests for Mutation Coverage")
    class BoundaryTests {

        @Test
        @DisplayName("Should handle email verification with null emailVerified")
        void shouldHandleNullEmailVerified() {
            // Arrange
            testUser.setEmailVerified(null);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Please verify your email before logging in");
        }

        @Test
        @DisplayName("Should handle expiration time calculation correctly")
        void shouldCalculateExpirationTimeCorrectly() {
            // Arrange
            long expectedExpiry = System.currentTimeMillis() + 86400000;
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(jwtUtil.generateToken(anyString(), anyLong(), anyString())).thenReturn("jwt-token");
            when(jwtUtil.extractExpiration(anyString())).thenReturn(new Date(expectedExpiry));
            when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            AuthResponse response = authService.login(loginRequest);

            // Assert
            assertThat(response.getExpiresIn()).isGreaterThan(0);
            assertThat(response.getExpiresIn()).isLessThanOrEqualTo(86400000);
        }
    }
}



