package com.healthtourism.doctorservice.mapper;

import com.healthtourism.doctorservice.dto.DoctorCreateRequest;
import com.healthtourism.doctorservice.dto.DoctorResponseDTO;
import com.healthtourism.doctorservice.dto.DoctorUpdateRequest;
import com.healthtourism.doctorservice.dto.HospitalSummaryDTO;
import com.healthtourism.doctorservice.entity.Doctor;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Doctor Mapper using MapStruct
 * 
 * Professional mapper for converting between Entity and DTOs.
 * MapStruct generates implementation at compile time for better performance.
 * 
 * Benefits:
 * - Type-safe mapping
 * - Compile-time error checking
 * - High performance (no reflection)
 * - Easy to maintain
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DoctorMapper {
    
    /**
     * Convert Entity to Response DTO
     * 
     * Handles:
     * - String specialization → List<String> specializations
     * - String languages → List<String> languages (comma-separated to List)
     * - Computes fullName
     * - Maps hospitalId to HospitalSummaryDTO (needs to be set manually in service)
     */
    @Mapping(target = "fullName", expression = "java(computeFullName(doctor))")
    @Mapping(target = "specializations", expression = "java(parseSpecializations(doctor.getSpecialization()))")
    @Mapping(target = "languages", expression = "java(parseLanguages(doctor.getLanguages()))")
    @Mapping(target = "currency", constant = "EUR") // Default currency, can be customized
    @Mapping(target = "hospital", ignore = true) // Set manually in service
    DoctorResponseDTO toResponseDTO(Doctor doctor);
    
    /**
     * Convert Create Request to Entity
     * 
     * Handles:
     * - List<String> specializations → String specialization (comma-separated)
     * - List<String> languages → String languages (comma-separated)
     * - System-managed fields are ignored (set in service)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "isAvailable", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "specialization", expression = "java(joinList(request.getSpecializations()))")
    @Mapping(target = "languages", expression = "java(convertListToLanguagesSet(request.getLanguages()))")
    @Mapping(target = "consultationFee", expression = "java(convertDoubleToBigDecimal(request.getConsultationFee()))")
    Doctor toEntity(DoctorCreateRequest request);
    
    /**
     * Update Entity from Update Request
     * 
     * Handles partial updates - only non-null fields are updated
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "hospitalId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "specialization", expression = "java(request.getSpecializations() != null && !request.getSpecializations().isEmpty() ? joinList(request.getSpecializations()) : doctor.getSpecialization())")
    @Mapping(target = "languages", expression = "java(request.getLanguages() != null && !request.getLanguages().isEmpty() ? convertListToLanguagesSet(request.getLanguages()) : doctor.getLanguages())")
    @Mapping(target = "consultationFee", expression = "java(request.getConsultationFee() != null ? convertDoubleToBigDecimal(request.getConsultationFee()) : doctor.getConsultationFee())")
    void updateEntityFromRequest(@MappingTarget Doctor doctor, DoctorUpdateRequest request);
    
    /**
     * List conversion
     */
    List<DoctorResponseDTO> toResponseDTOList(List<Doctor> doctors);
    
    // Helper methods (default methods in interface)
    
    /**
     * Compute full name: title + firstName + lastName
     */
    default String computeFullName(Doctor doctor) {
        if (doctor == null) {
            return null;
        }
        String title = doctor.getTitle() != null ? doctor.getTitle() + " " : "";
        String firstName = doctor.getFirstName() != null ? doctor.getFirstName() : "";
        String lastName = doctor.getLastName() != null ? doctor.getLastName() : "";
        return (title + firstName + " " + lastName).trim();
    }
    
    /**
     * Parse comma-separated specialization string to List
     */
    default List<String> parseSpecializations(String specialization) {
        if (specialization == null || specialization.trim().isEmpty()) {
            return List.of();
        }
        return List.of(specialization.split(","))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Set<String> (ElementCollection) to List<String> for DTO
     */
    default List<String> convertLanguagesSetToList(Set<String> languages) {
        if (languages == null || languages.isEmpty()) {
            return List.of();
        }
        return languages.stream()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Convert List<String> to Set<String> for Entity (ElementCollection)
     */
    default Set<String> convertListToLanguagesSet(List<String> languages) {
        if (languages == null || languages.isEmpty()) {
            return new HashSet<>();
        }
        return languages.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
    
    /**
     * Convert BigDecimal to Double for DTO
     */
    default Double convertBigDecimalToDouble(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.doubleValue();
    }
    
    /**
     * Convert Double to BigDecimal for Entity
     */
    default BigDecimal convertDoubleToBigDecimal(Double value) {
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf(value);
    }
    
    /**
     * Join List to comma-separated String (for specialization backward compatibility)
     */
    default String joinList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return String.join(", ", list);
    }
}

