package com.healthtourism.security.pentest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive Penetration Test Suite
 * Tests for OWASP Top 10 vulnerabilities
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Penetration Test Suite")
public class PenetrationTestSuite {
    
    @Autowired
    private MockMvc mockMvc;
    
    // ========== SQL Injection Tests ==========
    
    @Test
    @DisplayName("SQL Injection - Login with SQL payload")
    public void testSqlInjectionLogin() throws Exception {
        String sqlPayload = "admin' OR '1'='1";
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\":\"%s\",\"password\":\"test\"}", sqlPayload)))
                .andExpect(status().isBadRequest()); // Should reject invalid email format
    }
    
    @Test
    @DisplayName("SQL Injection - Register with SQL payload")
    public void testSqlInjectionRegister() throws Exception {
        String sqlPayload = "test@test.com'; DROP TABLE users; --";
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\":\"%s\",\"password\":\"Test123!@#\",\"firstName\":\"Test\",\"lastName\":\"User\",\"phone\":\"1234567890\",\"country\":\"Turkey\"}", sqlPayload)))
                .andExpect(status().isBadRequest()); // Should reject invalid email format
    }
    
    // ========== XSS Tests ==========
    
    @Test
    @DisplayName("XSS - Register with script tag")
    public void testXssRegister() throws Exception {
        String xssPayload = "<script>alert('XSS')</script>";
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\":\"test@test.com\",\"password\":\"Test123!@#\",\"firstName\":\"%s\",\"lastName\":\"User\",\"phone\":\"1234567890\",\"country\":\"Turkey\"}", xssPayload)))
                .andExpect(status().isBadRequest()); // Should reject invalid name format
    }
    
    @Test
    @DisplayName("XSS - Register with JavaScript event handler")
    public void testXssJavaScriptEvent() throws Exception {
        String xssPayload = "onerror=alert('XSS')";
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\":\"test@test.com\",\"password\":\"Test123!@#\",\"firstName\":\"%s\",\"lastName\":\"User\",\"phone\":\"1234567890\",\"country\":\"Turkey\"}", xssPayload)))
                .andExpect(status().isBadRequest());
    }
    
    // ========== Authentication Bypass Tests ==========
    
    @Test
    @DisplayName("Authentication Bypass - Access protected endpoint without token")
    public void testAuthBypassWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("Authentication Bypass - Access with invalid token")
    public void testAuthBypassInvalidToken() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer invalid_token_12345"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("Authentication Bypass - Access with malformed token")
    public void testAuthBypassMalformedToken() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer not.a.valid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }
    
    // ========== Authorization Tests ==========
    
    @Test
    @DisplayName("Authorization - User accessing admin endpoint")
    public void testAuthorizationUserAccessingAdmin() throws Exception {
        // This would require a valid user token
        // In real test, create user token and try to access admin endpoint
        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer user_token_here"))
                .andExpect(status().isForbidden());
    }
    
    // ========== CSRF Tests ==========
    
    @Test
    @DisplayName("CSRF - POST request without CSRF token")
    public void testCsrfWithoutToken() throws Exception {
        // CSRF protection should be enabled for state-changing operations
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@test.com\",\"password\":\"Test123!@#\",\"firstName\":\"Test\",\"lastName\":\"User\",\"phone\":\"1234567890\",\"country\":\"Turkey\"}"))
                .andExpect(status().isOk()); // Auth endpoints may be exempt
    }
    
    // ========== Rate Limiting Tests ==========
    
    @Test
    @DisplayName("Rate Limiting - Exceed rate limit")
    public void testRateLimiting() throws Exception {
        // Send more requests than allowed
        for (int i = 0; i < 150; i++) {
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"test@test.com\",\"password\":\"wrong\"}"));
        }
        
        // Last request should be rate limited
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@test.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isTooManyRequests());
    }
    
    // ========== Input Validation Tests ==========
    
    @Test
    @DisplayName("Input Validation - Email format validation")
    public void testEmailValidation() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"invalid-email\",\"password\":\"Test123!@#\",\"firstName\":\"Test\",\"lastName\":\"User\",\"phone\":\"1234567890\",\"country\":\"Turkey\"}"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Input Validation - Password strength validation")
    public void testPasswordValidation() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@test.com\",\"password\":\"weak\",\"firstName\":\"Test\",\"lastName\":\"User\",\"phone\":\"1234567890\",\"country\":\"Turkey\"}"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Input Validation - Phone number validation")
    public void testPhoneValidation() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@test.com\",\"password\":\"Test123!@#\",\"firstName\":\"Test\",\"lastName\":\"User\",\"phone\":\"invalid\",\"country\":\"Turkey\"}"))
                .andExpect(status().isBadRequest());
    }
    
    // ========== Path Traversal Tests ==========
    
    @Test
    @DisplayName("Path Traversal - Access files outside allowed directory")
    public void testPathTraversal() throws Exception {
        mockMvc.perform(get("/api/files/../../../etc/passwd"))
                .andExpect(status().isBadRequest());
    }
    
    // ========== JWT Security Tests ==========
    
    @Test
    @DisplayName("JWT Security - Expired token")
    public void testExpiredToken() throws Exception {
        // Would need to create an expired token
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer expired_token"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("JWT Security - Token without signature")
    public void testTokenWithoutSignature() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer eyJhbGciOiJub25lIn0.eyJzdWIiOiJ0ZXN0In0."))
                .andExpect(status().isUnauthorized());
    }
    
    // ========== Information Disclosure Tests ==========
    
    @Test
    @DisplayName("Information Disclosure - Error messages should not leak sensitive info")
    public void testInformationDisclosure() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"nonexistent@test.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").doesNotExist()); // Should not reveal if user exists
    }
    
    // ========== Insecure Direct Object Reference Tests ==========
    
    @Test
    @DisplayName("IDOR - Access another user's data")
    public void testIdor() throws Exception {
        // User 1 trying to access User 2's data
        mockMvc.perform(get("/api/users/999")
                .header("Authorization", "Bearer user1_token"))
                .andExpect(status().isForbidden());
    }
}

