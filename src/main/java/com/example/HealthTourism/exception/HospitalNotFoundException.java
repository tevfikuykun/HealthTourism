package com.example.HealthTourism.exception;

/**
 * Exception thrown when a hospital resource is not found
 */
public class HospitalNotFoundException extends RuntimeException {
    public HospitalNotFoundException(String message) {
        super(message);
    }
    
    public HospitalNotFoundException(Long id) {
        super("ID'si " + id + " olan hastane bulunamadı.");
    }
}
