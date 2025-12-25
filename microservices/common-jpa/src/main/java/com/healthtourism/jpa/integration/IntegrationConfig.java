package com.healthtourism.jpa.integration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Integration Configuration
 * 
 * Configuration class for integration services (Email, SMS, File Storage).
 * Services are conditionally enabled based on application properties.
 */
@Configuration
public class IntegrationConfig {
    
    // Email Service Configuration
    // SendGridEmailServiceImpl is automatically enabled if sendgrid.api.key is configured
    
    // SMS Service Configuration
    // TwilioSMSServiceImpl is automatically enabled if twilio.account.sid is configured
    
    // File Storage Configuration
    // AzureBlobStorageServiceImpl is enabled if azure.storage.enabled=true
    // AwsS3StorageServiceImpl is enabled if aws.s3.enabled=true
    // Only one should be enabled at a time
    
}

