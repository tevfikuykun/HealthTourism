package com.example.HealthTourism.mapper;

import com.example.HealthTourism.dto.VisaConsultancyDTO;
import com.example.HealthTourism.entity.VisaConsultancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for converting between VisaConsultancy entity and VisaConsultancyDTO
 * Automatically generates implementation at compile time
 * 
 * Note: requiredDocumentsList is populated manually in service layer
 */
@Mapper(componentModel = "spring")
public interface VisaConsultancyMapper {
    
    VisaConsultancyMapper INSTANCE = Mappers.getMapper(VisaConsultancyMapper.class);
    
    /**
     * Converts VisaConsultancy entity to VisaConsultancyDTO
     * Note: requiredDocumentsList should be populated in service layer using String.split(",")
     */
    @Mapping(target = "requiredDocuments", source = "description") // Using description as requiredDocuments source
    @Mapping(target = "requiredDocumentsList", ignore = true) // Populated manually in service
    VisaConsultancyDTO toDto(VisaConsultancy visaConsultancy);
    
    /**
     * Converts VisaConsultancyDTO to VisaConsultancy entity
     */
    @Mapping(target = "description", source = "requiredDocuments") // Map back to description
    VisaConsultancy toEntity(VisaConsultancyDTO dto);
    
    /**
     * Converts list of VisaConsultancy entities to list of VisaConsultancyDTOs
     */
    List<VisaConsultancyDTO> toDtoList(List<VisaConsultancy> visaConsultancies);
}

