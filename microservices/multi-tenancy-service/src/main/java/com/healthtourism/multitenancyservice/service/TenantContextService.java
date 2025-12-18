package com.healthtourism.multitenancyservice.service;

import com.healthtourism.multitenancyservice.entity.Tenant;
import com.healthtourism.multitenancyservice.repository.TenantRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Tenant Context Service
 * Manages tenant isolation and context
 */
@Service
public class TenantContextService {
    
    @Autowired
    private TenantRepository tenantRepository;
    
    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<Tenant> TENANT = new ThreadLocal<>();
    
    /**
     * Extract tenant from request (subdomain or header)
     */
    public Tenant extractTenant(HttpServletRequest request) {
        // Method 1: Extract from subdomain
        String host = request.getHeader("Host");
        if (host != null && host.contains(".")) {
            String subdomain = host.split("\\.")[0];
            Tenant tenant = tenantRepository.findBySubdomain(subdomain);
            if (tenant != null && tenant.getActive()) {
                setTenant(tenant);
                return tenant;
            }
        }
        
        // Method 2: Extract from X-Tenant-ID header
        String tenantId = request.getHeader("X-Tenant-ID");
        if (tenantId != null) {
            Tenant tenant = tenantRepository.findById(Long.parseLong(tenantId))
                .orElse(null);
            if (tenant != null && tenant.getActive()) {
                setTenant(tenant);
                return tenant;
            }
        }
        
        // Default tenant (for single-tenant mode)
        return getDefaultTenant();
    }
    
    /**
     * Set current tenant context
     */
    public void setTenant(Tenant tenant) {
        if (tenant != null) {
            TENANT_ID.set(tenant.getId().toString());
            TENANT.set(tenant);
        }
    }
    
    /**
     * Get current tenant ID
     */
    public String getCurrentTenantId() {
        return TENANT_ID.get();
    }
    
    /**
     * Get current tenant
     */
    public Tenant getCurrentTenant() {
        return TENANT.get();
    }
    
    /**
     * Clear tenant context
     */
    public void clearTenant() {
        TENANT_ID.remove();
        TENANT.remove();
    }
    
    /**
     * Check if user belongs to tenant
     */
    public boolean userBelongsToTenant(Long userId, Long tenantId) {
        // In production, check user-tenant relationship
        // For now, return true if tenant is active
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        return tenant != null && tenant.getActive();
    }
    
    /**
     * Get default tenant (for single-tenant mode)
     */
    private Tenant getDefaultTenant() {
        Tenant defaultTenant = tenantRepository.findBySubdomain("default");
        if (defaultTenant == null) {
            // Create default tenant
            defaultTenant = new Tenant();
            defaultTenant.setName("Default Tenant");
            defaultTenant.setSubdomain("default");
            defaultTenant.setDatabaseUrl("jdbc:mysql://localhost:3306/health_tourism");
            defaultTenant.setActive(true);
            defaultTenant.setPlan("ENTERPRISE");
            defaultTenant.setMaxUsers(10000);
            defaultTenant.setCurrentUsers(0);
            defaultTenant.setCreatedAt(java.time.LocalDateTime.now());
            defaultTenant = tenantRepository.save(defaultTenant);
        }
        setTenant(defaultTenant);
        return defaultTenant;
    }
}
