package com.healthtourism.jpa.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base Response DTO
 * 
 * Base class for all response DTOs.
 * Includes common fields like ID, timestamps, etc.
 */
@Data
public abstract class BaseResponseDTO implements BaseDTO, Serializable {
    
    /**
     * Entity ID
     */
    protected UUID id;
    
    /**
     * Created at timestamp
     */
    protected LocalDateTime createdAt;
    
    /**
     * Updated at timestamp (if applicable)
     */
    protected LocalDateTime updatedAt;
    
    /**
     * Created by user identifier (if applicable)
     */
    protected String createdBy;
    
    /**
     * Updated by user identifier (if applicable)
     */
    protected String updatedBy;
}

