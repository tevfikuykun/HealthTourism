package com.healthtourism.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginRequest {
    private String provider; // "google" or "facebook"
    private String id; // Google ID or Facebook ID
    private String email;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String accessToken; // OAuth2 access token for verification
}

