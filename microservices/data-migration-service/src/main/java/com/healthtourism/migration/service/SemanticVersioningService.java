package com.healthtourism.migration.service;

import com.healthtourism.migration.model.EntityVersion;
import com.healthtourism.migration.repository.EntityVersionRepository;
import com.github.everit.json.schema.Schema;
import com.github.everit.json.schema.loader.SchemaLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Semantic Versioning Service
 * Manages entity schema versions and automatic migrations
 */
@Service
public class SemanticVersioningService {
    
    @Autowired
    private EntityVersionRepository entityVersionRepository;
    
    @Autowired
    private DataMigrationService dataMigrationService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern SEMVER_PATTERN = Pattern.compile("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)$");
    
    /**
     * Register or update entity schema version
     */
    public EntityVersion registerSchema(String entityName, String version, String jsonSchema) {
        EntityVersion existing = entityVersionRepository.findByEntityName(entityName);
        
        if (existing != null) {
            // Check if version is newer
            int comparison = compareVersions(version, existing.getCurrentVersion());
            if (comparison > 0) {
                // Trigger migration
                try {
                    dataMigrationService.migrateEntity(entityName, existing.getCurrentVersion(), version);
                } catch (Exception e) {
                    // Log error but continue
                    System.err.println("Migration failed: " + e.getMessage());
                }
                
                existing.setCurrentVersion(version);
                existing.setSchemaJson(jsonSchema);
                return entityVersionRepository.save(existing);
            } else if (comparison == 0) {
                // Same version, just update schema
                existing.setSchemaJson(jsonSchema);
                return entityVersionRepository.save(existing);
            } else {
                throw new IllegalArgumentException("Version " + version + " is not newer than " + existing.getCurrentVersion());
            }
        } else {
            // First registration
            EntityVersion entityVersion = new EntityVersion();
            entityVersion.setEntityName(entityName);
            entityVersion.setCurrentVersion(version);
            entityVersion.setSchemaJson(jsonSchema);
            return entityVersionRepository.save(entityVersion);
        }
    }
    
    /**
     * Validate data against schema
     */
    public boolean validateData(String entityName, String jsonData) {
        EntityVersion entityVersion = entityVersionRepository.findByEntityName(entityName);
        if (entityVersion == null) {
            throw new IllegalArgumentException("Entity " + entityName + " not found");
        }
        
        try {
            JsonNode schemaJson = objectMapper.readTree(entityVersion.getSchemaJson());
            JsonNode dataJson = objectMapper.readTree(jsonData);
            
            Schema schema = SchemaLoader.load(schemaJson);
            schema.validate(dataJson);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Compare semantic versions
     * Returns: 1 if v1 > v2, -1 if v1 < v2, 0 if equal
     */
    public int compareVersions(String v1, String v2) {
        if (!SEMVER_PATTERN.matcher(v1).matches() || !SEMVER_PATTERN.matcher(v2).matches()) {
            throw new IllegalArgumentException("Invalid semantic version format");
        }
        
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        
        for (int i = 0; i < 3; i++) {
            int num1 = Integer.parseInt(parts1[i]);
            int num2 = Integer.parseInt(parts2[i]);
            
            if (num1 > num2) return 1;
            if (num1 < num2) return -1;
        }
        
        return 0;
    }
    
    /**
     * Get current version of entity
     */
    public String getCurrentVersion(String entityName) {
        EntityVersion entityVersion = entityVersionRepository.findByEntityName(entityName);
        return entityVersion != null ? entityVersion.getCurrentVersion() : null;
    }
    
    /**
     * Get all entity versions
     */
    public List<EntityVersion> getAllVersions() {
        return entityVersionRepository.findAll();
    }
}

