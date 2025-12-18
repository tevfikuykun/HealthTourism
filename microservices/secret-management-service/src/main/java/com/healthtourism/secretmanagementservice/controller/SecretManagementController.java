package com.healthtourism.secretmanagementservice.controller;

import com.healthtourism.secretmanagementservice.service.AWSSecretsManagerService;
import com.healthtourism.secretmanagementservice.service.VaultSecretService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/secrets")
@Tag(name = "Secret Management", description = "Secure secret management endpoints")
public class SecretManagementController {
    
    @Autowired
    private VaultSecretService vaultSecretService;
    
    @Autowired
    private AWSSecretsManagerService awsSecretsManagerService;
    
    @GetMapping("/database")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get database credentials", description = "Retrieve database credentials from secret store (Admin only)")
    public ResponseEntity<Map<String, String>> getDatabaseCredentials() {
        Map<String, String> credentials = vaultSecretService.getDatabaseCredentials();
        return ResponseEntity.ok(credentials);
    }
    
    @GetMapping("/jwt")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get JWT secret", description = "Retrieve JWT secret key (Admin only)")
    public ResponseEntity<Map<String, String>> getJwtSecret() {
        Map<String, String> response = new HashMap<>();
        response.put("secret", vaultSecretService.getJwtSecret());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api-keys")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get API keys", description = "Retrieve external API keys (Admin only)")
    public ResponseEntity<Map<String, String>> getApiKeys() {
        return ResponseEntity.ok(vaultSecretService.getApiKeys());
    }
    
    @PostMapping("/store")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Store secret", description = "Store a new secret in Vault (Admin only)")
    public ResponseEntity<Map<String, Object>> storeSecret(
            @RequestParam String secretKey,
            @RequestParam String secretValue) {
        
        vaultSecretService.storeSecret(secretKey, secretValue);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Secret stored successfully");
        response.put("key", secretKey);
        
        return ResponseEntity.ok(response);
    }
}
