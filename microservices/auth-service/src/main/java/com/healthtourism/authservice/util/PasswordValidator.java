package com.healthtourism.authservice.util;

import com.healthtourism.authservice.exception.WeakPasswordException;
import org.springframework.stereotype.Component;

/**
 * Password Validator
 * 
 * Validates password strength according to security requirements.
 * 
 * Requirements:
 * - Minimum 8 characters
 * - At least one uppercase letter
 * - At least one lowercase letter
 * - At least one digit
 * - At least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?)
 */
@Component
public class PasswordValidator {
    
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    
    /**
     * Validate password strength
     * 
     * @param password Password to validate
     * @throws WeakPasswordException if password doesn't meet requirements
     */
    public void validate(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new WeakPasswordException("Password cannot be empty");
        }
        
        if (password.length() < MIN_LENGTH) {
            throw new WeakPasswordException(
                String.format("Password must be at least %d characters long", MIN_LENGTH)
            );
        }
        
        if (password.length() > MAX_LENGTH) {
            throw new WeakPasswordException(
                String.format("Password must not exceed %d characters", MAX_LENGTH)
            );
        }
        
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> 
            "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0
        );
        
        StringBuilder errors = new StringBuilder();
        if (!hasUpper) {
            errors.append("Password must contain at least one uppercase letter. ");
        }
        if (!hasLower) {
            errors.append("Password must contain at least one lowercase letter. ");
        }
        if (!hasDigit) {
            errors.append("Password must contain at least one digit. ");
        }
        if (!hasSpecial) {
            errors.append("Password must contain at least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?). ");
        }
        
        if (errors.length() > 0) {
            throw new WeakPasswordException(errors.toString().trim());
        }
    }
    
    /**
     * Check if password is strong (without throwing exception)
     * 
     * @param password Password to check
     * @return true if password meets requirements
     */
    public boolean isStrong(String password) {
        try {
            validate(password);
            return true;
        } catch (WeakPasswordException e) {
            return false;
        }
    }
}

