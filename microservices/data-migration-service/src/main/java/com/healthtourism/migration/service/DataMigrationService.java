package com.healthtourism.migration.service;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Data Migration Service
 * Handles automatic data migration when entity schemas change
 */
@Service
public class DataMigrationService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    
    /**
     * Migrate entity data from old version to new version
     */
    public void migrateEntity(String entityName, String oldVersion, String newVersion) {
        // Determine migration type based on version change
        String[] oldParts = oldVersion.split("\\.");
        String[] newParts = newVersion.split("\\.");
        
        int majorOld = Integer.parseInt(oldParts[0]);
        int minorOld = Integer.parseInt(oldParts[1]);
        int patchOld = Integer.parseInt(oldParts[2]);
        
        int majorNew = Integer.parseInt(newParts[0]);
        int minorNew = Integer.parseInt(newParts[1]);
        int patchNew = Integer.parseInt(newParts[2]);
        
        // Major version change: Breaking changes
        if (majorNew > majorOld) {
            performMajorMigration(entityName, oldVersion, newVersion);
        }
        // Minor version change: New fields (backward compatible)
        else if (minorNew > minorOld) {
            performMinorMigration(entityName, oldVersion, newVersion);
        }
        // Patch version change: Bug fixes (no schema change)
        else if (patchNew > patchOld) {
            performPatchMigration(entityName, oldVersion, newVersion);
        }
    }
    
    /**
     * Major migration: Breaking changes
     * Requires data transformation
     */
    private void performMajorMigration(String entityName, String oldVersion, String newVersion) {
        // Execute Flyway migration script
        String migrationScript = String.format(
            "db/migration/V%s__%s_major_migration.sql",
            newVersion.replace(".", "_"),
            entityName.toLowerCase()
        );
        
        executeFlywayMigration(migrationScript);
        
        // Transform existing data
        transformDataForMajorVersion(entityName, oldVersion, newVersion);
    }
    
    /**
     * Minor migration: New fields (backward compatible)
     * Add new columns with default values
     */
    private void performMinorMigration(String entityName, String oldVersion, String newVersion) {
        String migrationScript = String.format(
            "db/migration/V%s__%s_minor_migration.sql",
            newVersion.replace(".", "_"),
            entityName.toLowerCase()
        );
        
        executeFlywayMigration(migrationScript);
    }
    
    /**
     * Patch migration: Bug fixes
     * No schema changes, just data corrections
     */
    private void performPatchMigration(String entityName, String oldVersion, String newVersion) {
        // Usually no migration needed for patches
        // But can include data fixes if needed
    }
    
    /**
     * Execute Flyway migration
     */
    private void executeFlywayMigration(String scriptPath) {
        try {
            Flyway flyway = Flyway.configure()
                .dataSource(jdbcTemplate.getDataSource())
                .locations("classpath:" + scriptPath)
                .baselineOnMigrate(true)
                .validateOnMigrate(false)
                .load();
            
            flyway.migrate();
        } catch (Exception e) {
            // Log error but don't fail if migration script doesn't exist
            System.err.println("Migration script not found: " + scriptPath + " - " + e.getMessage());
        }
    }
    
    /**
     * Transform data for major version change
     */
    private void transformDataForMajorVersion(String entityName, String oldVersion, String newVersion) {
        // Custom transformation logic based on entity
        String tableName = entityName.toLowerCase() + "s";
        
        // Example: Rename columns, transform data types, etc.
        // This would be customized per entity
        switch (entityName) {
            case "Patient":
                transformPatientData(tableName, oldVersion, newVersion);
                break;
            case "Reservation":
                transformReservationData(tableName, oldVersion, newVersion);
                break;
            default:
                // Generic transformation
                break;
        }
    }
    
    private void transformPatientData(String tableName, String oldVersion, String newVersion) {
        // Example: If version 2.0.0 adds a new required field "middleName"
        // Set default value for existing records
        jdbcTemplate.update(
            "ALTER TABLE " + tableName + " ADD COLUMN IF NOT EXISTS middle_name VARCHAR(255) DEFAULT ''"
        );
    }
    
    private void transformReservationData(String tableName, String oldVersion, String newVersion) {
        // Custom transformation for Reservation entity
    }
}

