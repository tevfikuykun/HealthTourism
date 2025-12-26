package com.example.HealthTourism.exception;

/**
 * Exception thrown when a translation service is not found
 */
public class TranslationServiceNotFoundException extends RuntimeException {
    public TranslationServiceNotFoundException(String message) {
        super(message);
    }
    
    public TranslationServiceNotFoundException(Long id) {
        super("Tercüman bulunamadı. ID: " + id);
    }
}

