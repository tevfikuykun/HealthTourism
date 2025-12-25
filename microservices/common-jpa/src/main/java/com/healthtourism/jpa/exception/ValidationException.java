package com.healthtourism.jpa.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Validation Exception
 * 
 * Thrown when business rule validations fail.
 * Can contain multiple validation errors.
 * Maps to HTTP 400 status code.
 */
public class ValidationException extends BusinessException {
    
    private final List<ValidationError> validationErrors;
    
    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
        this.validationErrors = new ArrayList<>();
    }
    
    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
        this.validationErrors = new ArrayList<>();
    }
    
    public ValidationException(ErrorCode errorCode, List<ValidationError> validationErrors) {
        super(errorCode, "Doğrulama hatası");
        this.validationErrors = validationErrors != null ? validationErrors : new ArrayList<>();
    }
    
    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }
    
    public boolean hasValidationErrors() {
        return validationErrors != null && !validationErrors.isEmpty();
    }
    
    /**
     * Validation Error
     * Represents a single field validation error
     */
    public static class ValidationError {
        private final String field;
        private final String message;
        private final Object rejectedValue;
        
        public ValidationError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }
        
        public ValidationError(String field, String message) {
            this(field, message, null);
        }
        
        public String getField() {
            return field;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Object getRejectedValue() {
            return rejectedValue;
        }
    }
}

