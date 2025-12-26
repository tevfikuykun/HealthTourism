package com.example.HealthTourism.mapper;

import com.example.HealthTourism.dto.TravelPackageDTO;
import com.example.HealthTourism.entity.TravelPackage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for converting between TravelPackage entity and TravelPackageDTO
 * Automatically generates implementation at compile time
 */
@Mapper(componentModel = "spring")
public interface TravelPackageMapper {
    
    TravelPackageMapper INSTANCE = Mappers.getMapper(TravelPackageMapper.class);
    
    /**
     * Converts TravelPackage entity to TravelPackageDTO
     * Custom mappings for nested entity fields
     */
    @Mapping(target = "hospitalId", source = "hospital.id")
    @Mapping(target = "hospitalName", source = "hospital.name")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "doctorName", expression = "java(travelPackage.getDoctor() != null ? travelPackage.getDoctor().getTitle() + \" \" + travelPackage.getDoctor().getFirstName() + \" \" + travelPackage.getDoctor().getLastName() : null)")
    @Mapping(target = "accommodationId", source = "accommodation.id")
    @Mapping(target = "accommodationName", source = "accommodation.name")
    TravelPackageDTO toDto(TravelPackage travelPackage);
    
    /**
     * Converts TravelPackageDTO to TravelPackage entity
     * Note: Relationships should be set manually in service layer
     */
    @Mapping(target = "hospital", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    TravelPackage toEntity(TravelPackageDTO dto);
    
    /**
     * Converts list of TravelPackage entities to list of TravelPackageDTOs
     */
    List<TravelPackageDTO> toDtoList(List<TravelPackage> travelPackages);
}

