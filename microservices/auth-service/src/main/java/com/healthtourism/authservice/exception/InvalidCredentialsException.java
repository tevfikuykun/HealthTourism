package com.healthtourism.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when login credentials are invalid
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends AuthException {
    
    public InvalidCredentialsException() {
        super(AuthErrorCode.INVALID_CREDENTIALS);
    }
    
    public InvalidCredentialsException(String message) {
        super(AuthErrorCode.INVALID_CREDENTIALS, message);
    }
}

