package com.healthtourism.jpa.config.entity;

import com.healthtourism.jpa.entity.Patient;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Patient Entity Configuration
 * 
 * Configures indexes and constraints for Patient entity.
 * Uses Fluent API approach similar to Entity Framework's IEntityTypeConfiguration.
 */
@Component
public class PatientConfiguration {
    
    @Autowired(required = false)
    private EntityManagerFactory entityManagerFactory;
    
    /**
     * Configure Patient entity indexes after entity manager factory is initialized
     * This method is called after Spring context is fully initialized
     */
    @PostConstruct
    public void configure() {
        if (entityManagerFactory == null) {
            return;
        }
        
        // Indexes are defined via @Table annotation on the entity class
        // This configuration class can be extended for additional programmatic configuration
        // For now, indexes are defined using @Index annotation in Patient entity
    }
    
    /**
     * Get configuration documentation for Patient entity
     * 
     * Configured indexes:
     * - idx_patients_email: Unique index on email (for fast lookups)
     * - idx_patients_national_id: Index on national_id (for TC No searches)
     * - idx_patients_passport_number: Index on passport_number (for passport searches)
     * - idx_patients_phone: Index on phone (for phone searches)
     * - idx_patients_email_national_id: Composite index on email and national_id
     */
    public String getConfigurationInfo() {
        return """
            Patient Entity Configuration:
            - Unique index on email: idx_patients_email
            - Index on national_id: idx_patients_national_id
            - Index on passport_number: idx_patients_passport_number
            - Index on phone: idx_patients_phone
            - Composite index on email and national_id: idx_patients_email_national_id
            """;
    }
}

