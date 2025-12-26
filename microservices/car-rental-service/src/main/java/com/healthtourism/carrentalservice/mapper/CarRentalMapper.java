package com.healthtourism.carrentalservice.mapper;

import com.healthtourism.carrentalservice.dto.CarRentalDTO;
import com.healthtourism.carrentalservice.entity.CarRental;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper for converting between CarRental entity and CarRentalDTO
 * Automatically generates implementation at compile time
 */
@Mapper(componentModel = "spring")
public interface CarRentalMapper {
    
    CarRentalMapper INSTANCE = Mappers.getMapper(CarRentalMapper.class);
    
    /**
     * Converts CarRental entity to CarRentalDTO
     */
    CarRentalDTO toDto(CarRental carRental);
    
    /**
     * Converts CarRentalDTO to CarRental entity
     */
    CarRental toEntity(CarRentalDTO dto);
    
    /**
     * Converts list of CarRental entities to list of CarRentalDTOs
     */
    java.util.List<CarRentalDTO> toDtoList(java.util.List<CarRental> carRentals);
}

