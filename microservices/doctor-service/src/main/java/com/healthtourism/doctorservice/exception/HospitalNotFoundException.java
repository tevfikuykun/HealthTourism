package com.healthtourism.doctorservice.exception;

/**
 * Hospital Not Found Exception
 * 
 * Thrown when a hospital with the specified ID is not found or not active.
 */
public class HospitalNotFoundException extends ResourceNotFoundException {
    
    public HospitalNotFoundException(Long id) {
        super("Hastane bulunamadı: " + id);
    }
    
    public HospitalNotFoundException(String message) {
        super(message);
    }
}

