package com.healthtourism.accommodationservice.exception;

/**
 * Exception thrown when an accommodation resource is not found
 */
public class AccommodationNotFoundException extends ResourceNotFoundException {
    public AccommodationNotFoundException(String message) {
        super(message);
    }
    
    public AccommodationNotFoundException(Long id) {
        super("ID'si " + id + " olan konaklama bulunamadı.");
    }
}
