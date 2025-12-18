package com.healthtourism.secretmanagementservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * HashiCorp Vault Secret Service
 * Manages secrets securely using Vault
 */
@Service
public class VaultSecretService {
    
    @Autowired(required = false)
    private VaultTemplate vaultTemplate;
    
    @Value("${vault.enabled:false}")
    private boolean vaultEnabled;
    
    @Value("${vault.secret.path:secret/data/health-tourism}")
    private String vaultSecretPath;
    
    /**
     * Get secret from Vault
     */
    public String getSecret(String secretKey) {
        if (!vaultEnabled || vaultTemplate == null) {
            // Fallback to environment variable or default
            return System.getenv(secretKey) != null ? System.getenv(secretKey) : getDefaultSecret(secretKey);
        }
        
        try {
            String path = vaultSecretPath + "/" + secretKey;
            VaultResponseSupport<Map> response = vaultTemplate.read(path, Map.class);
            
            if (response != null && response.getData() != null) {
                Map<String, Object> data = (Map<String, Object>) response.getData().get("data");
                return data != null ? (String) data.get("value") : null;
            }
        } catch (Exception e) {
            System.err.println("Error reading secret from Vault: " + e.getMessage());
            // Fallback to environment variable
            return System.getenv(secretKey);
        }
        
        return null;
    }
    
    /**
     * Store secret in Vault
     */
    public void storeSecret(String secretKey, String secretValue) {
        if (!vaultEnabled || vaultTemplate == null) {
            System.err.println("Vault not enabled, secret not stored");
            return;
        }
        
        try {
            String path = vaultSecretPath + "/" + secretKey;
            Map<String, Object> data = new HashMap<>();
            data.put("value", secretValue);
            
            Map<String, Object> secretData = new HashMap<>();
            secretData.put("data", data);
            
            vaultTemplate.write(path, secretData);
        } catch (Exception e) {
            System.err.println("Error storing secret in Vault: " + e.getMessage());
            throw new RuntimeException("Failed to store secret", e);
        }
    }
    
    /**
     * Get database credentials
     */
    public Map<String, String> getDatabaseCredentials() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", getSecret("database.username"));
        credentials.put("password", getSecret("database.password"));
        credentials.put("url", getSecret("database.url"));
        return credentials;
    }
    
    /**
     * Get JWT secret
     */
    public String getJwtSecret() {
        return getSecret("jwt.secret");
    }
    
    /**
     * Get API keys
     */
    public Map<String, String> getApiKeys() {
        Map<String, String> apiKeys = new HashMap<>();
        apiKeys.put("google.speech", getSecret("api.google.speech"));
        apiKeys.put("azure.speech", getSecret("api.azure.speech"));
        apiKeys.put("azure.region", getSecret("api.azure.region"));
        return apiKeys;
    }
    
    /**
     * Get IPFS encryption key
     */
    public String getIpfsEncryptionKey() {
        return getSecret("ipfs.encryption.key");
    }
    
    /**
     * Default secrets (for development only)
     */
    private String getDefaultSecret(String secretKey) {
        // WARNING: Only for development!
        Map<String, String> defaults = new HashMap<>();
        defaults.put("database.username", "root");
        defaults.put("database.password", "root");
        defaults.put("jwt.secret", "dev-jwt-secret-change-in-production");
        defaults.put("ipfs.encryption.key", "dev-ipfs-key-change-in-production");
        
        return defaults.getOrDefault(secretKey, null);
    }
}
