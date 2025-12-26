package com.example.HealthTourism.mapper;

import com.example.HealthTourism.dto.HospitalDTO;
import com.example.HealthTourism.entity.Hospital;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between Hospital entity and HospitalDTO
 * Automatically generates implementation at compile time
 * 
 * Note: Specializations and activeDoctorCount require custom mapping
 * since they come from related entities (doctors).
 */
@Mapper(componentModel = "spring")
public interface HospitalMapper {
    
    HospitalMapper INSTANCE = Mappers.getMapper(HospitalMapper.class);
    
    /**
     * Converts Hospital entity to HospitalDTO
     * Note: specializations and activeDoctorCount should be set manually
     * after calling this method, or use toDtoWithDetails instead.
     */
    @Mapping(target = "specializations", ignore = true)
    @Mapping(target = "activeDoctorCount", ignore = true)
    HospitalDTO toDto(Hospital hospital);
    
    /**
     * Converts HospitalDTO to Hospital entity
     */
    Hospital toEntity(HospitalDTO dto);
    
    /**
     * Converts list of Hospital entities to list of HospitalDTOs
     */
    List<HospitalDTO> toDtoList(List<Hospital> hospitals);
    
    /**
     * Default method to extract specializations from hospital's doctors
     * This method is used in service layer to enrich DTO with specialization data
     */
    default List<String> extractSpecializations(Hospital hospital) {
        if (hospital.getDoctors() == null) {
            return List.of();
        }
        return hospital.getDoctors().stream()
                .filter(d -> d.getIsAvailable() != null && d.getIsAvailable())
                .map(d -> d.getSpecialization())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Default method to count active doctors
     */
    default Integer countActiveDoctors(Hospital hospital) {
        if (hospital.getDoctors() == null) {
            return 0;
        }
        return (int) hospital.getDoctors().stream()
                .filter(d -> d.getIsAvailable() != null && d.getIsAvailable())
                .count();
    }
}

