package com.healthtourism.authservice.exception;

/**
 * Base Authentication Exception
 * 
 * All authentication-related exceptions should extend this class.
 */
public class AuthException extends RuntimeException {
    
    private final AuthErrorCode errorCode;
    
    public AuthException(AuthErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }
    
    public AuthException(AuthErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public AuthException(AuthErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public AuthErrorCode getErrorCode() {
        return errorCode;
    }
}

