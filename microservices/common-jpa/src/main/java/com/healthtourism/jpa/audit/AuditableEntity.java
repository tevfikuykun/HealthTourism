package com.healthtourism.jpa.audit;

import com.healthtourism.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base Auditable Entity
 * Hibernate Envers ile otomatik audit
 * 
 * @deprecated Use {@link BaseEntity} instead. BaseEntity provides:
 * - UUID (Sequential) primary keys for better security
 * - Soft delete functionality (isDeleted flag)
 * - Complete audit trail (CreatedAt, CreatedBy, UpdatedAt, UpdatedBy)
 * - Better scalability and security
 * 
 * Migration guide:
 * - Replace "extends AuditableEntity" with "extends BaseEntity"
 * - Change entity ID type from Long to UUID
 * - Update repositories to use BaseRepository instead of JpaRepository
 * - Update service methods to use UUID instead of Long for IDs
 */
@Deprecated
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Audited
@Data
public abstract class AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @Version
    @Column(name = "version")
    private Long version; // Optimistic locking
}








