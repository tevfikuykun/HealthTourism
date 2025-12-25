package com.healthtourism.jpa.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Base Request DTO
 * 
 * Base class for all request DTOs (Create/Update operations).
 * Request DTOs typically don't include ID, createdAt, etc.
 * (They are set by the system)
 */
@Data
public abstract class BaseRequestDTO implements Serializable {
    
    /**
     * Request DTOs should not expose entity IDs directly
     * IDs are typically passed as path variables in REST APIs
     */
}

