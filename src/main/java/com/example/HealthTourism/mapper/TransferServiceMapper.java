package com.example.HealthTourism.mapper;

import com.example.HealthTourism.dto.TransferServiceDTO;
import com.example.HealthTourism.entity.TransferService;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for converting between TransferService entity and TransferServiceDTO
 * Automatically generates implementation at compile time
 */
@Mapper(componentModel = "spring")
public interface TransferServiceMapper {
    
    TransferServiceMapper INSTANCE = Mappers.getMapper(TransferServiceMapper.class);
    
    /**
     * Converts TransferService entity to TransferServiceDTO
     */
    TransferServiceDTO toDto(TransferService transferService);
    
    /**
     * Converts TransferServiceDTO to TransferService entity
     */
    TransferService toEntity(TransferServiceDTO dto);
    
    /**
     * Converts list of TransferService entities to list of TransferServiceDTOs
     */
    List<TransferServiceDTO> toDtoList(List<TransferService> transferServices);
}

