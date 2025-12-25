package com.healthtourism.jpa.entity.seed;

import com.healthtourism.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Treatment Branch Entity - Seed Data
 * 
 * Represents medical treatment branches/specialties (e.g., Cardiology, Orthopedics, Oncology).
 * This is reference data that should be seeded during initial setup.
 */
@Entity
@Table(
    name = "treatment_branches",
    indexes = {
        @Index(name = "idx_treatment_branches_code", columnList = "code", unique = true),
        @Index(name = "idx_treatment_branches_name", columnList = "name"),
        @Index(name = "idx_treatment_branches_active", columnList = "is_active")
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
public class TreatmentBranch extends BaseEntity {
    
    /**
     * Unique code for the treatment branch (e.g., "CARDIO", "ORTHO", "ONCO")
     */
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;
    
    /**
     * Treatment branch name (e.g., "Cardiology", "Orthopedics", "Oncology")
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * Treatment branch name in Turkish (if applicable)
     */
    @Column(name = "name_tr", length = 100)
    private String nameTr;
    
    /**
     * Description of the treatment branch
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * Icon/Image URL for the treatment branch
     */
    @Column(name = "icon_url", length = 500)
    private String iconUrl;
    
    /**
     * Whether the treatment branch is active/available
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * Display order for UI
     */
    @Column(name = "display_order")
    private Integer displayOrder;
    
    /**
     * Category/group for organizing branches (e.g., "Surgical", "Medical", "Diagnostic")
     */
    @Column(name = "category", length = 50)
    private String category;
}

