package com.healthtourism.jpa.mapper;

import com.healthtourism.jpa.dto.BaseRequestDTO;
import com.healthtourism.jpa.dto.BaseResponseDTO;
import com.healthtourism.jpa.entity.BaseEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Request/Response Mapper Interface
 * 
 * Specialized mapper for Request/Response DTO pattern.
 * Separates Request DTO (for create/update) from Response DTO (for read).
 * 
 * @param <E> Entity type extending BaseEntity
 * @param <Req> Request DTO type
 * @param <Res> Response DTO type
 */
public interface RequestResponseMapper<E extends BaseEntity, Req extends BaseRequestDTO, Res extends BaseResponseDTO> {
    
    /**
     * Map Request DTO to Entity (for create operations)
     * 
     * @param requestDTO Request DTO
     * @return Entity
     */
    E toEntity(Req requestDTO);
    
    /**
     * Map Entity to Response DTO (for read operations)
     * 
     * @param entity Entity
     * @return Response DTO
     */
    Res toResponseDTO(E entity);
    
    /**
     * Map Entity list to Response DTO list
     * 
     * @param entities Entity list
     * @return Response DTO list
     */
    default List<Res> toResponseDTOList(List<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Update entity from Request DTO (for update operations)
     * Merges Request DTO fields into existing entity
     * 
     * @param requestDTO Request DTO with new values
     * @param entity Existing entity to update
     */
    void updateEntityFromRequest(Req requestDTO, E entity);
}

