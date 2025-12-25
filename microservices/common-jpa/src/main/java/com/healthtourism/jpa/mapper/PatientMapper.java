package com.healthtourism.jpa.mapper;

import com.healthtourism.jpa.dto.patient.PatientRequestDTO;
import com.healthtourism.jpa.dto.patient.PatientResponseDTO;
import com.healthtourism.jpa.entity.Patient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

/**
 * Patient Mapper
 * 
 * Maps between Patient entity and Patient DTOs.
 * Implements RequestResponseMapper for Request/Response DTO pattern.
 */
@Component
public class PatientMapper implements RequestResponseMapper<Patient, PatientRequestDTO, PatientResponseDTO> {
    
    @Override
    public Patient toEntity(PatientRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        
        Patient patient = new Patient();
        patient.setFirstName(requestDTO.getFirstName());
        patient.setLastName(requestDTO.getLastName());
        patient.setEmail(requestDTO.getEmail());
        patient.setPhone(requestDTO.getPhone());
        patient.setDateOfBirth(requestDTO.getDateOfBirth());
        patient.setNationalId(requestDTO.getNationalId());
        patient.setPassportNumber(requestDTO.getPassportNumber());
        
        // Map gender enum
        if (requestDTO.getGender() != null) {
            try {
                patient.setGender(Patient.Gender.valueOf(requestDTO.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Invalid gender, leave as null
            }
        }
        
        patient.setBloodType(requestDTO.getBloodType());
        patient.setMedicalHistory(requestDTO.getMedicalHistory());
        patient.setAllergies(requestDTO.getAllergies());
        
        return patient;
    }
    
    @Override
    public PatientResponseDTO toResponseDTO(Patient entity) {
        if (entity == null) {
            return null;
        }
        
        PatientResponseDTO dto = new PatientResponseDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setFullName(entity.getFirstName() + " " + entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setDateOfBirth(entity.getDateOfBirth());
        
        // Calculate age from date of birth
        if (entity.getDateOfBirth() != null) {
            dto.setAge(Period.between(entity.getDateOfBirth(), LocalDate.now()).getYears());
        }
        
        // Map gender enum to string
        if (entity.getGender() != null) {
            dto.setGender(entity.getGender().name());
        }
        
        dto.setBloodType(entity.getBloodType());
        dto.setMedicalHistory(entity.getMedicalHistory());
        dto.setAllergies(entity.getAllergies());
        
        // Map audit fields from BaseEntity
        if (entity instanceof com.healthtourism.jpa.entity.BaseEntity) {
            com.healthtourism.jpa.entity.BaseEntity baseEntity = (com.healthtourism.jpa.entity.BaseEntity) entity;
            dto.setCreatedAt(baseEntity.getCreatedAt());
            dto.setUpdatedAt(baseEntity.getUpdatedAt());
            dto.setCreatedBy(baseEntity.getCreatedBy());
            dto.setUpdatedBy(baseEntity.getUpdatedBy());
        }
        
        // Security: National ID and Passport Number should be masked or excluded
        // For now, including them but they should be masked in production
        // dto.setNationalId(maskSensitiveData(entity.getNationalId()));
        // dto.setPassportNumber(maskSensitiveData(entity.getPassportNumber()));
        dto.setNationalId(entity.getNationalId());
        dto.setPassportNumber(entity.getPassportNumber());
        
        return dto;
    }
    
    @Override
    public void updateEntityFromRequest(PatientRequestDTO requestDTO, Patient entity) {
        if (requestDTO == null || entity == null) {
            return;
        }
        
        // Update fields from request DTO
        if (requestDTO.getFirstName() != null) {
            entity.setFirstName(requestDTO.getFirstName());
        }
        if (requestDTO.getLastName() != null) {
            entity.setLastName(requestDTO.getLastName());
        }
        if (requestDTO.getEmail() != null) {
            entity.setEmail(requestDTO.getEmail());
        }
        if (requestDTO.getPhone() != null) {
            entity.setPhone(requestDTO.getPhone());
        }
        if (requestDTO.getDateOfBirth() != null) {
            entity.setDateOfBirth(requestDTO.getDateOfBirth());
        }
        if (requestDTO.getNationalId() != null) {
            entity.setNationalId(requestDTO.getNationalId());
        }
        if (requestDTO.getPassportNumber() != null) {
            entity.setPassportNumber(requestDTO.getPassportNumber());
        }
        if (requestDTO.getGender() != null) {
            try {
                entity.setGender(Patient.Gender.valueOf(requestDTO.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Invalid gender, skip
            }
        }
        if (requestDTO.getBloodType() != null) {
            entity.setBloodType(requestDTO.getBloodType());
        }
        if (requestDTO.getMedicalHistory() != null) {
            entity.setMedicalHistory(requestDTO.getMedicalHistory());
        }
        if (requestDTO.getAllergies() != null) {
            entity.setAllergies(requestDTO.getAllergies());
        }
    }
    
    /**
     * Mask sensitive data (e.g., show only last 4 digits)
     * 
     * @param data Sensitive data
     * @return Masked data
     */
    private String maskSensitiveData(String data) {
        if (data == null || data.length() <= 4) {
            return "****";
        }
        return "****" + data.substring(data.length() - 4);
    }
}

