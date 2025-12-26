package com.healthtourism.accommodationservice.mapper;

import com.healthtourism.accommodationservice.dto.AccommodationDTO;
import com.healthtourism.accommodationservice.entity.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper for converting between Accommodation entity and AccommodationDTO
 * Automatically generates implementation at compile time
 */
@Mapper(componentModel = "spring")
public interface AccommodationMapper {
    
    AccommodationMapper INSTANCE = Mappers.getMapper(AccommodationMapper.class);
    
    /**
     * Converts Accommodation entity to AccommodationDTO
     */
    AccommodationDTO toDto(Accommodation accommodation);
    
    /**
     * Converts AccommodationDTO to Accommodation entity
     */
    Accommodation toEntity(AccommodationDTO dto);
    
    /**
     * Converts list of Accommodation entities to list of AccommodationDTOs
     */
    java.util.List<AccommodationDTO> toDtoList(java.util.List<Accommodation> accommodations);
}

