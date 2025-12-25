package com.healthtourism.jpa.specification;

import com.healthtourism.jpa.entity.BaseEntity;
import org.springframework.data.jpa.domain.Specification;

/**
 * JPA Specifications for BaseEntity
 * 
 * Provides reusable specifications for common queries:
 * - Filter non-deleted entities (soft delete)
 * - Filter deleted entities
 * - Combine with other specifications
 */
public class BaseEntitySpecification {
    
    /**
     * Specification to filter out soft-deleted entities
     * Use this in repositories to automatically exclude deleted records
     * 
     * Example:
     * <pre>
     * {@code
     * repository.findAll(notDeleted().and(otherSpecification))
     * }
     * </pre>
     */
    public static <T extends BaseEntity> Specification<T> notDeleted() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("isDeleted"), false);
    }
    
    /**
     * Specification to filter only soft-deleted entities
     * Useful for recovery or audit purposes
     */
    public static <T extends BaseEntity> Specification<T> deleted() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("isDeleted"), true);
    }
    
    /**
     * Specification to include all entities (deleted and non-deleted)
     * Use with caution - typically only for admin/audit queries
     */
    public static <T extends BaseEntity> Specification<T> all() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.conjunction(); // Always true
    }
    
    /**
     * Specification to filter by created date range
     */
    public static <T extends BaseEntity> Specification<T> createdBetween(
            java.time.LocalDateTime startDate, 
            java.time.LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
    }
    
    /**
     * Specification to filter by updated date range
     */
    public static <T extends BaseEntity> Specification<T> updatedBetween(
            java.time.LocalDateTime startDate, 
            java.time.LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.between(root.get("updatedAt"), startDate, endDate);
    }
    
    /**
     * Specification to filter by created by user
     */
    public static <T extends BaseEntity> Specification<T> createdBy(String userIdentifier) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("createdBy"), userIdentifier);
    }
    
    /**
     * Specification to filter by updated by user
     */
    public static <T extends BaseEntity> Specification<T> updatedBy(String userIdentifier) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("updatedBy"), userIdentifier);
    }
}

