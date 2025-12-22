package com.healthtourism.migration.controller;

import com.healthtourism.migration.model.EntityVersion;
import com.healthtourism.migration.service.SemanticVersioningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Migration Controller
 * REST API for entity version management and migration
 */
@RestController
@RequestMapping("/api/migration")
public class MigrationController {
    
    @Autowired
    private SemanticVersioningService versioningService;
    
    /**
     * Register new entity schema version
     */
    @PostMapping("/schema")
    public ResponseEntity<EntityVersion> registerSchema(
            @RequestBody Map<String, String> request) {
        String entityName = request.get("entityName");
        String version = request.get("version");
        String jsonSchema = request.get("jsonSchema");
        
        EntityVersion entityVersion = versioningService.registerSchema(entityName, version, jsonSchema);
        return ResponseEntity.ok(entityVersion);
    }
    
    /**
     * Validate data against schema
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateData(
            @RequestBody Map<String, String> request) {
        String entityName = request.get("entityName");
        String jsonData = request.get("jsonData");
        
        boolean isValid = versioningService.validateData(entityName, jsonData);
        return ResponseEntity.ok(Map.of(
            "valid", isValid,
            "entityName", entityName
        ));
    }
    
    /**
     * Get current version of entity
     */
    @GetMapping("/version/{entityName}")
    public ResponseEntity<Map<String, String>> getVersion(@PathVariable String entityName) {
        String version = versioningService.getCurrentVersion(entityName);
        return ResponseEntity.ok(Map.of(
            "entityName", entityName,
            "version", version != null ? version : "not-found"
        ));
    }
    
    /**
     * Get all entity versions
     */
    @GetMapping("/versions")
    public ResponseEntity<List<EntityVersion>> getAllVersions() {
        return ResponseEntity.ok(versioningService.getAllVersions());
    }
}






