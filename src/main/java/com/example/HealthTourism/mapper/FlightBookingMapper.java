package com.example.HealthTourism.mapper;

import com.example.HealthTourism.dto.FlightBookingDTO;
import com.example.HealthTourism.entity.FlightBooking;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for converting between FlightBooking entity and FlightBookingDTO
 * Automatically generates implementation at compile time
 */
@Mapper(componentModel = "spring")
public interface FlightBookingMapper {
    
    FlightBookingMapper INSTANCE = Mappers.getMapper(FlightBookingMapper.class);
    
    /**
     * Converts FlightBooking entity to FlightBookingDTO
     */
    FlightBookingDTO toDto(FlightBooking flightBooking);
    
    /**
     * Converts FlightBookingDTO to FlightBooking entity
     */
    FlightBooking toEntity(FlightBookingDTO dto);
    
    /**
     * Converts list of FlightBooking entities to list of FlightBookingDTOs
     */
    List<FlightBookingDTO> toDtoList(List<FlightBooking> flightBookings);
}

