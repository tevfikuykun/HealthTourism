package com.example.HealthTourism.mapper;

import com.example.HealthTourism.dto.TranslationServiceDTO;
import com.example.HealthTourism.entity.TranslationService;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for converting between TranslationService entity and TranslationServiceDTO
 * Automatically generates implementation at compile time
 */
@Mapper(componentModel = "spring")
public interface TranslationServiceMapper {
    
    TranslationServiceMapper INSTANCE = Mappers.getMapper(TranslationServiceMapper.class);
    
    /**
     * Converts TranslationService entity to TranslationServiceDTO
     */
    TranslationServiceDTO toDto(TranslationService translationService);
    
    /**
     * Converts TranslationServiceDTO to TranslationService entity
     */
    TranslationService toEntity(TranslationServiceDTO dto);
    
    /**
     * Converts list of TranslationService entities to list of TranslationServiceDTOs
     */
    List<TranslationServiceDTO> toDtoList(List<TranslationService> translationServices);
}

