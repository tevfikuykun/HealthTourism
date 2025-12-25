package com.healthtourism.jpa.config.entity;

import org.springframework.stereotype.Component;

/**
 * User Entity Configuration Template
 * 
 * This is a template/example configuration for User entity.
 * Copy this to your service module (auth-service, user-service) and adjust as needed.
 * 
 * Configured indexes:
 * - idx_users_email: Unique index on email (for authentication and lookups)
 * - idx_users_phone: Index on phone (for phone-based searches)
 * - idx_users_national_id: Index on national_id/TC_No (for Turkish ID searches) - if field exists
 * - idx_users_passport_number: Index on passport_number (for passport searches) - if field exists
 * - idx_users_email_phone: Composite index on email and phone
 * 
 * Usage Example in User Entity:
 * <pre>
 * {@code
 * @Entity
 * @Table(
 *     name = "users",
 *     indexes = {
 *         @Index(name = "idx_users_email", columnList = "email", unique = true),
 *         @Index(name = "idx_users_phone", columnList = "phone"),
 *         @Index(name = "idx_users_national_id", columnList = "national_id"),
 *         @Index(name = "idx_users_passport_number", columnList = "passport_number"),
 *         @Index(name = "idx_users_email_phone", columnList = "email,phone")
 *     }
 * )
 * public class User extends BaseEntity {
 *     // ...
 * }
 * }
 * </pre>
 */
@Component
public class UserConfiguration {
    
    /**
     * Get configuration documentation for User entity
     * 
     * @return Configuration info
     */
    public String getConfigurationInfo() {
        return """
            User Entity Configuration:
            - Unique index on email: idx_users_email (for authentication)
            - Index on phone: idx_users_phone (for phone searches)
            - Index on national_id (TC No): idx_users_national_id (for Turkish ID searches)
            - Index on passport_number: idx_users_passport_number (for passport searches)
            - Composite index on email and phone: idx_users_email_phone
            
            Note: Add these indexes to your User entity's @Table annotation.
            """;
    }
}

