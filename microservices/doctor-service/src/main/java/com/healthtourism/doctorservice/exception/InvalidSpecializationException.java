package com.healthtourism.doctorservice.exception;

/**
 * Invalid Specialization Exception
 * 
 * Thrown when an invalid or non-existent specialization is provided.
 */
public class InvalidSpecializationException extends BusinessException {
    
    public InvalidSpecializationException(String specialization) {
        super("Geçersiz uzmanlık alanı: " + specialization);
    }
    
    public InvalidSpecializationException(String message, Throwable cause) {
        super(message, cause);
    }
}

