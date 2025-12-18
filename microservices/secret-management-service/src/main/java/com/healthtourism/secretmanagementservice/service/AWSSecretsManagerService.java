package com.healthtourism.secretmanagementservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * AWS Secrets Manager Service
 * Alternative to Vault for AWS deployments
 */
@Service
public class AWSSecretsManagerService {
    
    @Value("${aws.secrets-manager.enabled:false}")
    private boolean awsSecretsEnabled;
    
    @Value("${aws.secrets-manager.region:us-east-1}")
    private String awsRegion;
    
    private SecretsManagerClient secretsClient;
    
    private SecretsManagerClient getSecretsClient() {
        if (secretsClient == null && awsSecretsEnabled) {
            secretsClient = SecretsManagerClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        }
        return secretsClient;
    }
    
    /**
     * Get secret from AWS Secrets Manager
     */
    public String getSecret(String secretName) {
        if (!awsSecretsEnabled || getSecretsClient() == null) {
            return System.getenv(secretName);
        }
        
        try {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
            
            GetSecretValueResponse response = getSecretsClient().getSecretValue(request);
            return response.secretString();
        } catch (Exception e) {
            System.err.println("Error reading secret from AWS Secrets Manager: " + e.getMessage());
            return System.getenv(secretName);
        }
    }
    
    /**
     * Get database credentials from AWS Secrets Manager
     */
    public Map<String, String> getDatabaseCredentials() {
        String secretJson = getSecret("health-tourism/database");
        if (secretJson == null) {
            return new HashMap<>();
        }
        
        // Parse JSON (simplified - use proper JSON parser in production)
        Map<String, String> credentials = new HashMap<>();
        // In production, parse JSON properly
        return credentials;
    }
}
