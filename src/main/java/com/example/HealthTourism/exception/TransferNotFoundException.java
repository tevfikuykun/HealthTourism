package com.example.HealthTourism.exception;

/**
 * Exception thrown when a transfer service is not found
 */
public class TransferNotFoundException extends RuntimeException {
    public TransferNotFoundException(String message) {
        super(message);
    }
    
    public TransferNotFoundException(Long id) {
        super("Transfer hizmeti bulunamadı. ID: " + id);
    }
}

