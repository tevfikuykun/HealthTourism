package com.healthtourism.multitenancyservice.controller;

import com.healthtourism.multitenancyservice.entity.Tenant;
import com.healthtourism.multitenancyservice.service.TenantContextService;
import com.healthtourism.multitenancyservice.repository.TenantRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tenants")
@Tag(name = "Tenant Management", description = "Multi-tenancy management endpoints")
public class TenantController {
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Autowired
    private TenantContextService tenantContextService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create tenant", description = "Create a new tenant (Admin only)")
    public ResponseEntity<Tenant> createTenant(@RequestBody Map<String, Object> request) {
        Tenant tenant = new Tenant();
        tenant.setName(request.get("name").toString());
        tenant.setSubdomain(request.get("subdomain").toString());
        tenant.setDatabaseUrl(request.getOrDefault("databaseUrl", 
            "jdbc:mysql://localhost:3306/" + request.get("subdomain").toString() + "_db").toString());
        tenant.setActive(true);
        tenant.setPlan(request.getOrDefault("plan", "BASIC").toString());
        tenant.setMaxUsers(Integer.parseInt(request.getOrDefault("maxUsers", "100").toString()));
        tenant.setCurrentUsers(0);
        tenant.setCreatedAt(LocalDateTime.now());
        
        Tenant saved = tenantRepository.save(tenant);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/current")
    @Operation(summary = "Get current tenant", description = "Get current tenant context")
    public ResponseEntity<Tenant> getCurrentTenant() {
        Tenant tenant = tenantContextService.getCurrentTenant();
        return ResponseEntity.ok(tenant);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all tenants", description = "Get all tenants (Admin only)")
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(tenantRepository.findAll());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get tenant by ID", description = "Get tenant details (Admin only)")
    public ResponseEntity<Tenant> getTenantById(@PathVariable Long id) {
        return tenantRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update tenant status", description = "Activate or deactivate tenant (Admin only)")
    public ResponseEntity<Tenant> updateTenantStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {
        return tenantRepository.findById(id)
            .map(tenant -> {
                tenant.setActive(active);
                tenant.setUpdatedAt(LocalDateTime.now());
                return ResponseEntity.ok(tenantRepository.save(tenant));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
