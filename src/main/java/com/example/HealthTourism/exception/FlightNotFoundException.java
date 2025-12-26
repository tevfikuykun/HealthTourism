package com.example.HealthTourism.exception;

/**
 * Exception thrown when a flight booking resource is not found
 */
public class FlightNotFoundException extends RuntimeException {
    public FlightNotFoundException(String message) {
        super(message);
    }
    
    public FlightNotFoundException(Long id) {
        super("ID'si " + id + " olan uçuş bulunamadı.");
    }
}
