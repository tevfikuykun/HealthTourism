package com.healthtourism.jpa.entity.seed;

import com.healthtourism.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Role Entity - Seed Data
 * 
 * Represents user roles in the system (e.g., USER, ADMIN, DOCTOR, NURSE).
 * This is reference data that should be seeded during initial setup.
 */
@Entity
@Table(
    name = "roles",
    indexes = {
        @Index(name = "idx_roles_code", columnList = "code", unique = true),
        @Index(name = "idx_roles_name", columnList = "name"),
        @Index(name = "idx_roles_active", columnList = "is_active")
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {
    
    /**
     * Unique role code (e.g., "USER", "ADMIN", "DOCTOR", "NURSE")
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    /**
     * Role name (e.g., "User", "Administrator", "Doctor", "Nurse")
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * Role description
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * Whether the role is active/available
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * Default role assigned to new users
     */
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;
    
    /**
     * System role that cannot be deleted or modified
     */
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;
    
    /**
     * Permission level (for hierarchical permission systems)
     * Lower numbers = higher permissions
     */
    @Column(name = "permission_level")
    private Integer permissionLevel;
}

