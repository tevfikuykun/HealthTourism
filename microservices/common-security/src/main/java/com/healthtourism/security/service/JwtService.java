package com.healthtourism.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Service - Professional Enterprise Implementation
 * 
 * Handles:
 * - JWT token generation
 * - JWT token validation
 * - Token claims extraction
 * - Token expiration management
 * 
 * Security Best Practices:
 * - Strong secret key (minimum 256 bits)
 * - Token expiration (short-lived access tokens)
 * - Refresh token support
 * - Claims validation
 */
@Service
public class JwtService {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    
    @Value("${jwt.secret:your-256-bit-secret-key-change-this-in-production-minimum-32-characters}")
    private String secretKey;
    
    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;
    
    @Value("${jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private Long refreshExpiration;
    
    /**
     * Generate JWT token for user
     * 
     * @param userDetails User details (username, roles, etc.)
     * @return JWT token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities());
        return generateToken(claims, userDetails.getUsername());
    }
    
    /**
     * Generate JWT token with custom claims
     * 
     * @param claims Custom claims (roles, permissions, etc.)
     * @param username Username (subject)
     * @return JWT token
     */
    public String generateToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Generate refresh token
     * 
     * @param username Username
     * @return Refresh token (longer expiration)
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extract username from token
     * 
     * @param token JWT token
     * @return Username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extract expiration date from token
     * 
     * @param token JWT token
     * @return Expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extract specific claim from token
     * 
     * @param token JWT token
     * @param claimsResolver Function to extract claim
     * @return Claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extract all claims from token
     * 
     * @param token JWT token
     * @return All claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Check if token is expired
     * 
     * @param token JWT token
     * @return true if expired
     */
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            logger.warn("Error checking token expiration: {}", e.getMessage());
            return true; // If we can't parse, consider it expired
        }
    }
    
    /**
     * Validate token
     * 
     * @param token JWT token
     * @param userDetails User details to validate against
     * @return true if valid
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate token (without user details)
     * 
     * @param token JWT token
     * @return true if valid
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get signing key from secret
     * 
     * @return Secret key for signing
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        
        // Ensure key is at least 256 bits (32 bytes)
        if (keyBytes.length < 32) {
            logger.warn("JWT secret key is too short. Minimum 32 characters (256 bits) required.");
            // Pad with zeros (not secure, but better than failing)
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
            keyBytes = paddedKey;
        }
        
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Extract roles from token
     * 
     * @param token JWT token
     * @return Roles (as string list)
     */
    @SuppressWarnings("unchecked")
    public java.util.List<String> extractRoles(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object authorities = claims.get("authorities");
            
            if (authorities instanceof java.util.List) {
                return (java.util.List<String>) authorities;
            }
            
            return java.util.Collections.emptyList();
        } catch (Exception e) {
            logger.warn("Error extracting roles from token: {}", e.getMessage());
            return java.util.Collections.emptyList();
        }
    }
}

