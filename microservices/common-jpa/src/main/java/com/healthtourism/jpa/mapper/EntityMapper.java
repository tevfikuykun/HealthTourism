package com.healthtourism.jpa.mapper;

import com.healthtourism.jpa.dto.BaseDTO;
import com.healthtourism.jpa.entity.BaseEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Entity Mapper Interface
 * 
 * Similar to AutoMapper/MapStruct pattern.
 * Provides mapping between Entity and DTO objects.
 * 
 * This interface ensures:
 * - Entity <-> DTO conversion
 * - Request DTO -> Entity conversion
 * - Entity -> Response DTO conversion
 * - List conversion utilities
 * 
 * @param <E> Entity type extending BaseEntity
 * @param <D> DTO type extending BaseDTO
 */
public interface EntityMapper<E extends BaseEntity, D extends BaseDTO> {
    
    /**
     * Map Entity to DTO
     * 
     * @param entity Entity to map
     * @return DTO
     */
    D toDTO(E entity);
    
    /**
     * Map DTO to Entity
     * 
     * @param dto DTO to map
     * @return Entity
     */
    E toEntity(D dto);
    
    /**
     * Map Entity list to DTO list
     * 
     * @param entities Entity list
     * @return DTO list
     */
    default List<D> toDTOList(List<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Map DTO list to Entity list
     * 
     * @param dtos DTO list
     * @return Entity list
     */
    default List<E> toEntityList(List<D> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Update entity from DTO
     * Merges DTO fields into existing entity without changing ID
     * 
     * @param dto DTO with new values
     * @param entity Existing entity to update
     */
    void updateEntityFromDTO(D dto, E entity);
}

