package com.healthtourism.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when password does not meet strength requirements
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WeakPasswordException extends AuthException {
    
    public WeakPasswordException(String message) {
        super(AuthErrorCode.WEAK_PASSWORD, message);
    }
}

