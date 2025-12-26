package com.example.HealthTourism.exception;

/**
 * Exception thrown when a travel package is not found
 */
public class TravelPackageNotFoundException extends RuntimeException {
    public TravelPackageNotFoundException(String message) {
        super(message);
    }
    
    public TravelPackageNotFoundException(Long id) {
        super("Paket bulunamadı. ID: " + id);
    }
}

