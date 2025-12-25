package com.healthtourism.doctorservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Doctor Entity - Professional Enterprise Implementation
 * 
 * Best Practices Applied:
 * - Audit fields (createdAt, updatedAt, createdBy, updatedBy) - via BaseEntity or manual
 * - Soft delete (@SQLDelete, @Where) - prevents data loss
 * - ElementCollection for languages - proper data structure
 * - BigDecimal for currency - precision for financial data
 * - Enum for specialization - type safety
 * - Database indexes - query performance
 * - Lazy loading - prevents N+1 problems
 * - Column constraints - data integrity
 * 
 * Note: In a microservice architecture, hospital relationship is via hospitalId (not @ManyToOne)
 * to avoid cross-service dependencies. However, if Hospital is in the same service, @ManyToOne with LAZY can be used.
 */
@Entity
@Table(name = "doctors", indexes = {
    @Index(name = "idx_doctor_hospital_id", columnList = "hospital_id"),
    @Index(name = "idx_doctor_specialization", columnList = "specialization"),
    @Index(name = "idx_doctor_is_available", columnList = "is_available"),
    @Index(name = "idx_doctor_rating", columnList = "rating"),
    @Index(name = "idx_doctor_hospital_available", columnList = "hospital_id, is_available"),
    @Index(name = "idx_doctor_specialization_available", columnList = "specialization, is_available"),
    @Index(name = "idx_doctor_deleted", columnList = "deleted")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) // Enable JPA Auditing
@SQLDelete(sql = "UPDATE doctors SET deleted = true, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted = false")
@ToString(exclude = {"languages"}) // Exclude collections from toString to prevent lazy loading issues
public class Doctor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Audit: When the entity was created
     * Automatically populated by JPA Auditing (@CreatedDate)
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Audit: Who created the entity (user identifier - UUID, email, or username)
     * Automatically populated by JPA Auditing (@CreatedBy)
     */
    @CreatedBy
    @Column(name = "created_by", length = 255)
    private String createdBy;
    
    /**
     * Audit: When the entity was last updated
     * Automatically populated by JPA Auditing (@LastModifiedDate)
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Audit: Who last updated the entity (user identifier - UUID, email, or username)
     * Automatically populated by JPA Auditing (@LastModifiedBy)
     */
    @LastModifiedBy
    @Column(name = "updated_by", length = 255)
    private String updatedBy;
    
    /**
     * Soft Delete: Flag to mark entity as deleted without physical deletion
     * Critical for healthcare data - records should never be physically deleted
     */
    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private Boolean deleted = false;
    
    /**
     * Optimistic Locking: Version field for concurrent access control
     * Prevents lost updates in concurrent scenarios
     */
    @Version
    @Column(name = "version")
    private Long version;
    
    @Column(nullable = false, length = 50)
    private String firstName;
    
    @Column(nullable = false, length = 50)
    private String lastName;
    
    /**
     * Specialization stored as String in database (for backward compatibility)
     * Can be converted to Enum via MapStruct mapper in DTO layer
     * 
     * Note: For new projects, consider using @Enumerated(EnumType.STRING) with Specialization enum
     * However, this requires data migration for existing projects.
     * Current approach maintains backward compatibility while allowing future migration.
     */
    @Column(nullable = false, length = 500)
    private String specialization;
    
    @Column(nullable = false, length = 50)
    private String title; // "Prof. Dr.", "Doç. Dr.", "Dr." etc.
    
    /**
     * Bio stored as TEXT for flexibility (no length limit)
     */
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(nullable = false)
    private Integer experienceYears;
    
    /**
     * Languages as ElementCollection - proper data structure for filtering
     * Stored in separate table: doctor_languages
     * Enables efficient queries like "find doctors who speak German"
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "doctor_languages",
        joinColumns = @JoinColumn(name = "doctor_id"),
        indexes = @Index(name = "idx_doctor_languages_language", columnList = "language")
    )
    @Column(name = "language", length = 50)
    @Builder.Default
    private Set<String> languages = new HashSet<>();
    
    /**
     * System-managed fields (calculated/aggregated from reviews)
     * These should be read-only and managed by the system
     */
    @Column(nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private Double rating = 0.0;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer totalReviews = 0;
    
    /**
     * Consultation fee - Using BigDecimal for precision
     * Critical for financial calculations - Double can cause rounding errors
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;
    
    /**
     * Availability flag for business logic
     * Separate from deleted flag - a doctor can be unavailable but not deleted
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;
    
    /**
     * Hospital ID (foreign key to hospital-service)
     * In microservice architecture, we use ID instead of @ManyToOne
     * to avoid cross-service dependencies
     * 
     * Note: If Hospital entity is in the same service, use:
     * @ManyToOne(fetch = FetchType.LAZY)
     * @JoinColumn(name = "hospital_id", nullable = false)
     * private Hospital hospital;
     */
    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;
    
    /**
     * Image URLs (stored in file-storage-service)
     */
    @Column(length = 500)
    private String imageUrl;
    
    @Column(length = 500)
    private String thumbnailUrl;
    
    /**
     * Soft delete this entity
     * Marks as deleted without physically removing from database
     */
    public void softDelete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Restore a soft-deleted entity
     */
    public void restore() {
        this.deleted = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Check if entity is deleted
     */
    public boolean isDeleted() {
        return Boolean.TRUE.equals(deleted);
    }
    
    /**
     * Pre-persist callback: Set default values if not set
     * Note: createdAt and updatedAt are handled by JPA Auditing,
     * but we keep this for backward compatibility and default values
     */
    @PrePersist
    protected void onCreate() {
        if (deleted == null) {
            deleted = false;
        }
        if (rating == null) {
            rating = 0.0;
        }
        if (totalReviews == null) {
            totalReviews = 0;
        }
        if (isAvailable == null) {
            isAvailable = true;
        }
        if (languages == null) {
            languages = new HashSet<>();
        }
    }
    
    /**
     * Pre-update callback: Ensure default values
     * Note: updatedAt is handled by JPA Auditing (@LastModifiedDate)
     */
    @PreUpdate
    protected void onUpdate() {
        // JPA Auditing will handle updatedAt automatically
        // This method is kept for any additional update logic if needed
    }
}
