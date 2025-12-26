package com.example.HealthTourism.exception;

/**
 * Exception thrown when a doctor resource is not found
 */
public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(String message) {
        super(message);
    }
    
    public DoctorNotFoundException(Long id) {
        super("ID'si " + id + " olan doktor bulunamadı.");
    }
}
