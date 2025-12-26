package com.example.HealthTourism.mapper;

import com.example.HealthTourism.dto.ReservationDTO;
import com.example.HealthTourism.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for converting between Reservation entity and ReservationDTO
 * Automatically generates implementation at compile time
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper {
    
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);
    
    /**
     * Converts Reservation entity to ReservationDTO
     * Custom mappings for nested entity fields
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", expression = "java(reservation.getUser().getFirstName() + \" \" + reservation.getUser().getLastName())")
    @Mapping(target = "hospitalId", source = "hospital.id")
    @Mapping(target = "hospitalName", source = "hospital.name")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "doctorName", expression = "java(reservation.getDoctor().getTitle() + \" \" + reservation.getDoctor().getFirstName() + \" \" + reservation.getDoctor().getLastName())")
    @Mapping(target = "accommodationId", source = "accommodation.id")
    @Mapping(target = "accommodationName", source = "accommodation.name")
    ReservationDTO toDto(Reservation reservation);
    
    /**
     * Converts ReservationDTO to Reservation entity
     * Note: This requires setting relationships manually in service layer
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "hospital", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    Reservation toEntity(ReservationDTO dto);
    
    /**
     * Converts list of Reservation entities to list of ReservationDTOs
     */
    List<ReservationDTO> toDtoList(List<Reservation> reservations);
}

