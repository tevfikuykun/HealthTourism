package com.healthtourism.multitenancyservice.filter;

import com.healthtourism.multitenancyservice.entity.Tenant;
import com.healthtourism.multitenancyservice.service.TenantContextService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Tenant Context Filter
 * Extracts tenant from request and sets tenant context
 */
@Component
@Order(1)
public class TenantContextFilter implements Filter {
    
    @Autowired
    private TenantContextService tenantContextService;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        try {
            // Extract and set tenant context
            Tenant tenant = tenantContextService.extractTenant(httpRequest);
            
            // Continue filter chain
            chain.doFilter(request, response);
        } finally {
            // Clear tenant context after request
            tenantContextService.clearTenant();
        }
    }
}
