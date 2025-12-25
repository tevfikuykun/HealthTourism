package com.healthtourism.doctorservice.exception;

/**
 * Doctor Not Found Exception
 * 
 * Thrown when a doctor with the specified ID is not found or not available.
 */
public class DoctorNotFoundException extends ResourceNotFoundException {
    
    public DoctorNotFoundException(Long id) {
        super("Doktor bulunamadı: " + id);
    }
    
    public DoctorNotFoundException(String message) {
        super(message);
    }
}

