package com.healthtourism.jpa.exception;

/**
 * File Validation Exception
 * 
 * Thrown when file validation fails (invalid MIME type, size, extension, etc.)
 */
public class FileValidationException extends RuntimeException {
    
    public FileValidationException(String message) {
        super(message);
    }
    
    public FileValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

