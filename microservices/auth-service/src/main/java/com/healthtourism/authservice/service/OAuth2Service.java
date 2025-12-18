package com.healthtourism.authservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * OAuth2 Social Login Service
 * Supports Google, Facebook, Apple login
 */
@Service
public class OAuth2Service {

    @Value("${oauth2.google.client-id:}")
    private String googleClientId;

    @Value("${oauth2.google.client-secret:}")
    private String googleClientSecret;

    @Value("${oauth2.facebook.client-id:}")
    private String facebookClientId;

    @Value("${oauth2.facebook.client-secret:}")
    private String facebookClientSecret;

    @Value("${oauth2.apple.client-id:}")
    private String appleClientId;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Authenticate with Google OAuth2
     */
    public Map<String, Object> authenticateWithGoogle(String authorizationCode, String redirectUri) {
        // Exchange authorization code for access token
        String tokenUrl = "https://oauth2.googleapis.com/token";
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String accessToken = (String) response.getBody().get("access_token");
            return getUserInfoFromGoogle(accessToken);
        }

        throw new RuntimeException("Failed to authenticate with Google");
    }

    /**
     * Get user info from Google
     */
    private Map<String, Object> getUserInfoFromGoogle(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
            userInfoUrl,
            HttpMethod.GET,
            entity,
            Map.class
        );

        return response.getBody();
    }

    /**
     * Authenticate with Facebook OAuth2
     */
    public Map<String, Object> authenticateWithFacebook(String authorizationCode, String redirectUri) {
        // Exchange authorization code for access token
        String tokenUrl = "https://graph.facebook.com/v18.0/oauth/access_token";
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", facebookClientId);
        params.add("client_secret", facebookClientSecret);
        params.add("redirect_uri", redirectUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String accessToken = (String) response.getBody().get("access_token");
            return getUserInfoFromFacebook(accessToken);
        }

        throw new RuntimeException("Failed to authenticate with Facebook");
    }

    /**
     * Get user info from Facebook
     */
    private Map<String, Object> getUserInfoFromFacebook(String accessToken) {
        String userInfoUrl = "https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken;
        
        ResponseEntity<Map> response = restTemplate.getForEntity(userInfoUrl, Map.class);
        return response.getBody();
    }

    /**
     * Authenticate with Apple OAuth2
     */
    public Map<String, Object> authenticateWithApple(String idToken) {
        // Apple uses ID token directly, verify and decode it
        // In production, use JWT library to verify Apple ID token
        // For now, return simulated response
        return Map.of(
            "id", "apple_user_id",
            "email", "user@example.com",
            "name", "Apple User"
        );
    }
}
