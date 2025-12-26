package com.example.HealthTourism.exception;

/**
 * Exception thrown when trying to register with an email that already exists
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
    
    public EmailAlreadyExistsException(String email) {
        super("Bu email adresi zaten kullanılıyor: " + email);
    }
}

