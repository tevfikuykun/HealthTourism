package com.healthtourism.jpa.validation;

import com.healthtourism.jpa.exception.ValidationException;

/**
 * Business Rule Validator Interface
 * 
 * Similar to FluentValidation pattern.
 * Validators are responsible for business rule validation
 * (not just data format validation).
 * 
 * Validation logic is separated from service classes,
 * making it easier to test and maintain.
 * 
 * @param <T> Type to validate
 */
public interface BusinessRuleValidator<T> {
    
    /**
     * Validate business rules
     * 
     * @param object Object to validate
     * @throws ValidationException if validation fails
     */
    void validate(T object) throws ValidationException;
    
    /**
     * Validate business rules (non-throwing version)
     * 
     * @param object Object to validate
     * @return true if valid, false otherwise
     */
    default boolean isValid(T object) {
        try {
            validate(object);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }
}

