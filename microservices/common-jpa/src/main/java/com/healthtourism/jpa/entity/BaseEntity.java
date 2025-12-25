package com.healthtourism.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base Entity for all domain entities
 * 
 * Features:
 * - UUID (Sequential) as primary key for security
 * - Audit Log: CreatedAt, CreatedBy, UpdatedAt, UpdatedBy
 * - Soft Delete: isDeleted flag to prevent data loss
 * - Optimistic Locking: version field for concurrent access control
 * 
 * All entities should extend this class to ensure consistency across the application.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class BaseEntity {
    
    /**
     * Primary key using UUID for security and scalability
     * Sequential UUID generation ensures better database performance
     */
    @Id
    @GeneratedValue(generator = "sequential-uuid")
    @GenericGenerator(name = "sequential-uuid", strategy = "com.healthtourism.jpa.util.SequentialUUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private UUID id;
    
    /**
     * Audit: When the entity was created
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Audit: Who created the entity (user identifier - UUID, email, or username)
     */
    @CreatedBy
    @Column(name = "created_by", length = 255)
    private String createdBy;
    
    /**
     * Audit: When the entity was last updated
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Audit: Who last updated the entity (user identifier - UUID, email, or username)
     */
    @LastModifiedBy
    @Column(name = "updated_by", length = 255)
    private String updatedBy;
    
    /**
     * Soft Delete: Flag to mark entity as deleted without physical deletion
     * Critical for healthcare data - records should never be physically deleted
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    /**
     * Optimistic Locking: Version field for concurrent access control
     * Prevents lost updates in concurrent scenarios
     */
    @Version
    @Column(name = "version")
    private Long version;
    
    /**
     * Soft delete this entity
     * Marks as deleted without physically removing from database
     */
    public void softDelete() {
        this.isDeleted = true;
    }
    
    /**
     * Restore a soft-deleted entity
     */
    public void restore() {
        this.isDeleted = false;
    }
    
    /**
     * Check if entity is deleted
     */
    public boolean isDeleted() {
        return Boolean.TRUE.equals(isDeleted);
    }
}

