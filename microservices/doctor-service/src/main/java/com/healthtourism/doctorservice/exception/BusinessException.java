package com.healthtourism.doctorservice.exception;

/**
 * Business Exception
 * 
 * Base exception for business rule violations.
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

