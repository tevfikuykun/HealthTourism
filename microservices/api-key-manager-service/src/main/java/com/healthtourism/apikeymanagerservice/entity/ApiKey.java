package com.healthtourism.apikeymanagerservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
public class ApiKey {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String keyValue; // Generated API key
    
    @Column(nullable = false)
    private String name; // API key name/description
    
    @Column(nullable = false)
    private String organization; // Organization name
    
    @Column(nullable = false)
    private Boolean active;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private Long rateLimit; // Requests per minute
    
    @Column(nullable = false)
    private Long totalRequests; // Total requests made
    
    @Column(nullable = false)
    private Long monthlyRequests; // Requests this month
    
    @Column(nullable = false)
    private String plan; // FREE, BASIC, PREMIUM, ENTERPRISE
    
    @Column
    private String allowedEndpoints; // Comma-separated list of allowed endpoints
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getKeyValue() { return keyValue; }
    public void setKeyValue(String keyValue) { this.keyValue = keyValue; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public Long getRateLimit() { return rateLimit; }
    public void setRateLimit(Long rateLimit) { this.rateLimit = rateLimit; }
    
    public Long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(Long totalRequests) { this.totalRequests = totalRequests; }
    
    public Long getMonthlyRequests() { return monthlyRequests; }
    public void setMonthlyRequests(Long monthlyRequests) { this.monthlyRequests = monthlyRequests; }
    
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    
    public String getAllowedEndpoints() { return allowedEndpoints; }
    public void setAllowedEndpoints(String allowedEndpoints) { this.allowedEndpoints = allowedEndpoints; }
}
