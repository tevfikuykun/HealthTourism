package com.healthtourism.vault.service;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.LogicalResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Vault Secret Service
 * Manages encryption keys, API keys, and secrets
 * Supports automatic key rotation
 */
@Service
public class VaultSecretService {
    
    @Value("${vault.address:http://localhost:8200}")
    private String vaultAddress;
    
    @Value("${vault.token:myroot}")
    private String vaultToken;
    
    @Value("${vault.secret.path:secret/healthtourism}")
    private String secretPath;
    
    private Vault vault;
    
    @PostConstruct
    public void init() {
        try {
            VaultConfig config = new VaultConfig()
                .address(vaultAddress)
                .token(vaultToken)
                .build();
            vault = new Vault(config);
        } catch (VaultException e) {
            throw new RuntimeException("Failed to initialize Vault", e);
        }
    }
    
    /**
     * Get secret from Vault
     */
    public String getSecret(String key) {
        try {
            LogicalResponse response = vault.logical().read(secretPath + "/" + key);
            if (response != null && response.getData() != null) {
                return response.getData().get(key);
            }
            return null;
        } catch (VaultException e) {
            // Return null instead of throwing for missing secrets
            return null;
        }
    }
    
    /**
     * Get AES encryption key
     */
    public String getAESKey() {
        String key = getSecret("aes-encryption-key");
        if (key == null) {
            // Generate and store if not exists
            key = generateAES256Key();
            storeSecret("aes-encryption-key", key);
        }
        return key;
    }
    
    /**
     * Get Polygon private key
     */
    public String getPolygonPrivateKey() {
        String key = getSecret("polygon-private-key");
        if (key == null) {
            throw new RuntimeException("Polygon private key not found in Vault. Please store it first.");
        }
        return key;
    }
    
    /**
     * Get API key for external service
     */
    public String getAPIKey(String serviceName) {
        return getSecret("api-keys/" + serviceName);
    }
    
    /**
     * Store secret in Vault
     */
    public void storeSecret(String key, String value) {
        try {
            Map<String, Object> secrets = new HashMap<>();
            secrets.put(key, value);
            vault.logical().write(secretPath + "/" + key, secrets);
        } catch (VaultException e) {
            throw new RuntimeException("Failed to store secret in Vault: " + key, e);
        }
    }
    
    /**
     * Rotate encryption key
     * Generates new key and updates in Vault
     */
    public String rotateAESKey() {
        try {
            // Generate new AES-256 key
            String newKey = generateAES256Key();
            
            // Store in Vault
            storeSecret("aes-encryption-key", newKey);
            
            // Keep old key for decryption of existing data
            String oldKey = getAESKey();
            storeSecret("aes-encryption-key-old", oldKey);
            
            return newKey;
        } catch (Exception e) {
            throw new RuntimeException("Failed to rotate AES key", e);
        }
    }
    
    /**
     * Generate AES-256 key
     */
    private String generateAES256Key() {
        java.security.SecureRandom random = new java.security.SecureRandom();
        byte[] key = new byte[32]; // 256 bits
        random.nextBytes(key);
        return java.util.Base64.getEncoder().encodeToString(key);
    }
    
    /**
     * Check if key rotation is needed
     */
    public boolean isKeyRotationNeeded(String keyName) {
        try {
            // Check key age (example: rotate every 90 days)
            LogicalResponse response = vault.logical().read(secretPath + "/" + keyName + "-metadata");
            // In production, check creation date and rotation policy
            return false; // Placeholder
        } catch (VaultException e) {
            return false;
        }
    }
}

