package com.example.HealthTourism.mapper;

import com.example.HealthTourism.dto.DoctorDTO;
import com.example.HealthTourism.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for converting between Doctor entity and DoctorDTO
 * Automatically generates implementation at compile time
 */
@Mapper(componentModel = "spring")
public interface DoctorMapper {
    
    DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);
    
    /**
     * Converts Doctor entity to DoctorDTO
     * Custom mapping for fullName field
     */
    @Mapping(target = "fullName", expression = "java(doctor.getTitle() + \" \" + doctor.getFirstName() + \" \" + doctor.getLastName())")
    @Mapping(target = "hospitalId", source = "hospital.id")
    @Mapping(target = "hospitalName", source = "hospital.name")
    DoctorDTO toDto(Doctor doctor);
    
    /**
     * Converts DoctorDTO to Doctor entity
     */
    Doctor toEntity(DoctorDTO dto);
    
    /**
     * Converts list of Doctor entities to list of DoctorDTOs
     */
    List<DoctorDTO> toDtoList(List<Doctor> doctors);
}

