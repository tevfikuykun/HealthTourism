package com.example.HealthTourism.exception;

/**
 * Exception thrown when a visa consultancy is not found
 */
public class VisaConsultancyNotFoundException extends RuntimeException {
    public VisaConsultancyNotFoundException(String message) {
        super(message);
    }
    
    public VisaConsultancyNotFoundException(Long id) {
        super("Vize danışmanı bulunamadı. ID: " + id);
    }
}

