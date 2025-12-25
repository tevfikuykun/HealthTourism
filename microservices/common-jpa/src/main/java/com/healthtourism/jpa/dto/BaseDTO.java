package com.healthtourism.jpa.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base DTO Interface
 * 
 * All DTOs should implement this interface or extend a base DTO class.
 * Provides common fields for DTOs.
 */
public interface BaseDTO extends Serializable {
    
    /**
     * Get DTO ID (if applicable)
     */
    UUID getId();
    
    /**
     * Set DTO ID
     */
    void setId(UUID id);
    
    /**
     * Get created at timestamp (if applicable)
     */
    LocalDateTime getCreatedAt();
    
    /**
     * Set created at timestamp
     */
    void setCreatedAt(LocalDateTime createdAt);
}

