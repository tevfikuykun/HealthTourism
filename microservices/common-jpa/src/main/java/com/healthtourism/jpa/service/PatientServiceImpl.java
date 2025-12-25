package com.healthtourism.jpa.service;

import com.healthtourism.jpa.entity.Patient;
import com.healthtourism.jpa.exception.ErrorCode;
import com.healthtourism.jpa.exception.ResourceNotFoundException;
import com.healthtourism.jpa.exception.ValidationException;
import com.healthtourism.jpa.repository.PatientRepository;
import com.healthtourism.jpa.validation.AgeValidator;
import com.healthtourism.jpa.validation.PhoneValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Patient Service Implementation
 * 
 * Implements PatientServiceInterface with business logic.
 * Uses validators for business rule validation.
 * Throws BusinessException for business rule violations.
 * 
 * This is an example of proper service implementation:
 * - Implements interface (dependency injection)
 * - Uses validators (separation of concerns)
 * - Throws custom exceptions (proper error handling)
 * - Transactional operations
 */
@Service
@Transactional
public class PatientServiceImpl implements PatientServiceInterface {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private PhoneValidator phoneValidator;
    
    @Autowired
    private AgeValidator ageValidator;
    
    @Override
    public Patient save(Patient patient) {
        // Basic validation
        if (patient == null) {
            throw new ValidationException(ErrorCode.REQUIRED_FIELD_MISSING, "Patient cannot be null");
        }
        
        // Validate phone number
        if (patient.getPhone() != null) {
            phoneValidator.validate(patient.getPhone());
        }
        
        // Validate age if date of birth is provided
        if (patient.getDateOfBirth() != null) {
            ageValidator.validate(patient.getDateOfBirth());
        }
        
        return patientRepository.save(patient);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> findById(UUID id) {
        return patientRepository.findByIdAndNotDeleted(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Patient> findAll() {
        return patientRepository.findAllActive();
    }
    
    @Override
    public void delete(UUID id) {
        Patient patient = findById(id)
            .orElseThrow(() -> ResourceNotFoundException.patientNotFound(id));
        
        patient.softDelete();
        patientRepository.save(patient);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return patientRepository.existsByIdAndNotDeleted(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long count() {
        return patientRepository.countActive();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> findByEmail(String email) {
        return patientRepository.findByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> findByPhone(String phone) {
        return patientRepository.findByPhone(phone);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Patient> findByFirstName(String firstName) {
        return patientRepository.findByFirstName(firstName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Patient> findByLastName(String lastName) {
        return patientRepository.findByLastName(lastName);
    }
    
    @Override
    public Patient createPatient(Patient patient) {
        // Validate required fields
        if (patient.getEmail() == null || patient.getEmail().trim().isEmpty()) {
            throw new ValidationException(ErrorCode.REQUIRED_FIELD_MISSING, "Email is required");
        }
        
        // Check if email already exists
        if (findByEmail(patient.getEmail()).isPresent()) {
            throw new ValidationException(
                ErrorCode.PATIENT_ALREADY_EXISTS,
                "Bu e-posta adresi ile kayıtlı hasta zaten mevcut: " + patient.getEmail()
            );
        }
        
        // Validate phone
        if (patient.getPhone() != null) {
            phoneValidator.validate(patient.getPhone());
        }
        
        // Validate age
        if (patient.getDateOfBirth() != null) {
            ageValidator.validate(patient.getDateOfBirth());
        }
        
        // Set default values
        patient.setIsDeleted(false);
        
        return save(patient);
    }
    
    @Override
    public Patient updatePatient(UUID id, Patient patient) {
        Patient existing = findById(id)
            .orElseThrow(() -> ResourceNotFoundException.patientNotFound(id));
        
        // Update fields
        if (patient.getFirstName() != null) {
            existing.setFirstName(patient.getFirstName());
        }
        if (patient.getLastName() != null) {
            existing.setLastName(patient.getLastName());
        }
        if (patient.getPhone() != null) {
            phoneValidator.validate(patient.getPhone());
            existing.setPhone(patient.getPhone());
        }
        if (patient.getDateOfBirth() != null) {
            ageValidator.validate(patient.getDateOfBirth());
            existing.setDateOfBirth(patient.getDateOfBirth());
        }
        if (patient.getEmail() != null && !patient.getEmail().equals(existing.getEmail())) {
            // Check if new email already exists
            if (findByEmail(patient.getEmail()).isPresent()) {
                throw new ValidationException(
                    ErrorCode.PATIENT_ALREADY_EXISTS,
                    "Bu e-posta adresi ile kayıtlı hasta zaten mevcut: " + patient.getEmail()
                );
            }
            existing.setEmail(patient.getEmail());
        }
        
        return save(existing);
    }
}

