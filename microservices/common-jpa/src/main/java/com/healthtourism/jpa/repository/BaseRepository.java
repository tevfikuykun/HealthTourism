package com.healthtourism.jpa.repository;

import com.healthtourism.jpa.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base Repository Interface for all entities extending BaseEntity
 * 
 * Provides common methods for:
 * - Soft delete operations
 * - Finding active (non-deleted) entities
 * - Restoring deleted entities
 * 
 * All repositories should extend this interface instead of JpaRepository directly
 * 
 * @param <T> Entity type extending BaseEntity
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {
    
    /**
     * Find entity by ID excluding soft-deleted entities
     * 
     * @param id Entity ID
     * @return Optional entity if found and not deleted
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.isDeleted = false")
    Optional<T> findByIdAndNotDeleted(@Param("id") UUID id);
    
    /**
     * Find all active (non-deleted) entities
     * 
     * @return List of active entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.isDeleted = false")
    List<T> findAllActive();
    
    /**
     * Soft delete entity by ID
     * Marks entity as deleted without physically removing it
     * 
     * @param id Entity ID
     * @return Number of affected rows
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isDeleted = true WHERE e.id = :id")
    int softDeleteById(@Param("id") UUID id);
    
    /**
     * Restore soft-deleted entity by ID
     * 
     * @param id Entity ID
     * @return Number of affected rows
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isDeleted = false WHERE e.id = :id")
    int restoreById(@Param("id") UUID id);
    
    /**
     * Hard delete entity by ID (physical deletion)
     * Use with extreme caution - only for admin operations
     * 
     * @param id Entity ID
     */
    @Modifying
    @Query("DELETE FROM #{#entityName} e WHERE e.id = :id")
    void hardDeleteById(@Param("id") UUID id);
    
    /**
     * Count active (non-deleted) entities
     * 
     * @return Count of active entities
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.isDeleted = false")
    long countActive();
    
    /**
     * Check if entity exists and is not deleted
     * 
     * @param id Entity ID
     * @return true if entity exists and is active
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.id = :id AND e.isDeleted = false")
    boolean existsByIdAndNotDeleted(@Param("id") UUID id);
}

