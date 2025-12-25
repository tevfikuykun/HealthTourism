package com.healthtourism.jpa.service;

import com.healthtourism.jpa.entity.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base Service Interface
 * 
 * All service interfaces should extend this interface.
 * Provides common CRUD operations for entities extending BaseEntity.
 * 
 * This pattern ensures:
 * - Dependency injection through interfaces
 * - Service contracts are clearly defined
 * - Testability (easier to mock)
 * - Loose coupling between layers
 * 
 * @param <T> Entity type extending BaseEntity
 */
public interface BaseService<T extends BaseEntity> {
    
    /**
     * Save entity (create or update)
     * 
     * @param entity Entity to save
     * @return Saved entity
     */
    T save(T entity);
    
    /**
     * Find entity by ID (only active/non-deleted)
     * 
     * @param id Entity ID
     * @return Optional entity
     */
    Optional<T> findById(UUID id);
    
    /**
     * Find all active entities
     * 
     * @return List of active entities
     */
    List<T> findAll();
    
    /**
     * Delete entity (soft delete)
     * 
     * @param id Entity ID
     */
    void delete(UUID id);
    
    /**
     * Check if entity exists and is active
     * 
     * @param id Entity ID
     * @return true if exists and active
     */
    boolean existsById(UUID id);
    
    /**
     * Count active entities
     * 
     * @return Count of active entities
     */
    long count();
}

