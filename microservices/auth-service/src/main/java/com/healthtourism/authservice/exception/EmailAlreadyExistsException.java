package com.healthtourism.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when email is already registered
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends AuthException {
    
    public EmailAlreadyExistsException(String email) {
        super(AuthErrorCode.EMAIL_ALREADY_IN_USE, "Email already registered: " + email);
    }
}

