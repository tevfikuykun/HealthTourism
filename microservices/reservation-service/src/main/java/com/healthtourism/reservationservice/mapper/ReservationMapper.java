package com.healthtourism.reservationservice.mapper;

import com.healthtourism.reservationservice.dto.ReservationDTO;
import com.healthtourism.reservationservice.dto.ReservationRequestDTO;
import com.healthtourism.reservationservice.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Reservation Mapper using MapStruct
 * 
 * Professional mapper for converting between Entity and DTOs.
 * MapStruct generates implementation at compile time for better performance.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper {
    
    /**
     * Convert Entity to Response DTO
     */
    ReservationDTO toDTO(Reservation reservation);
    
    /**
     * Convert Request DTO to Entity
     * System-managed fields are ignored (set in service)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservationNumber", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "doctorFeeSnapshot", ignore = true)
    @Mapping(target = "accommodationDailyPriceSnapshot", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    Reservation toEntity(ReservationRequestDTO request);
    
    /**
     * Convert List of Entities to List of DTOs
     */
    List<ReservationDTO> toDTOList(List<Reservation> reservations);
}

