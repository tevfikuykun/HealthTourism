package com.healthtourism.reservationservice.exception;

/**
 * Business Rule Exception
 * 
 * Thrown when a business rule is violated.
 */
public class BusinessRuleException extends RuntimeException {
    
    public BusinessRuleException(String message) {
        super(message);
    }
    
    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}

