package com.healthtourism.jpa.service;

import com.healthtourism.jpa.entity.Patient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Patient Service Interface
 * 
 * Defines the contract for Patient business operations.
 * Implementation should be in PatientServiceImpl.
 * 
 * This interface ensures:
 * - Dependency injection through interface
 * - Clear service contract
 * - Easy testing (mocking)
 * - Loose coupling
 */
public interface PatientServiceInterface extends BaseService<Patient> {
    
    /**
     * Find patient by email
     * 
     * @param email Patient email
     * @return Optional patient
     */
    Optional<Patient> findByEmail(String email);
    
    /**
     * Find patient by phone
     * 
     * @param phone Patient phone
     * @return Optional patient
     */
    Optional<Patient> findByPhone(String phone);
    
    /**
     * Find patients by first name
     * 
     * @param firstName First name
     * @return List of patients
     */
    List<Patient> findByFirstName(String firstName);
    
    /**
     * Find patients by last name
     * 
     * @param lastName Last name
     * @return List of patients
     */
    List<Patient> findByLastName(String lastName);
    
    /**
     * Create patient with validation
     * 
     * @param patient Patient to create
     * @return Created patient
     */
    Patient createPatient(Patient patient);
    
    /**
     * Update patient with validation
     * 
     * @param id Patient ID
     * @param patient Updated patient data
     * @return Updated patient
     */
    Patient updatePatient(UUID id, Patient patient);
}

