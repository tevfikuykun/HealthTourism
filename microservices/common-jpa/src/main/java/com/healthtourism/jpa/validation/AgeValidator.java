package com.healthtourism.jpa.validation;

import com.healthtourism.jpa.exception.ErrorCode;
import com.healthtourism.jpa.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Age Validator
 * 
 * Validates age constraints for patients and users.
 * Ensures age limits are respected according to business rules.
 */
@Component
public class AgeValidator implements BusinessRuleValidator<LocalDate> {
    
    private static final int MIN_AGE = 0; // Minimum age (babies allowed)
    private static final int MAX_AGE = 150; // Maximum reasonable age
    
    // Default age limits
    private int minAge = MIN_AGE;
    private int maxAge = MAX_AGE;
    
    @Override
    public void validate(LocalDate dateOfBirth) throws ValidationException {
        validate(dateOfBirth, minAge, maxAge);
    }
    
    /**
     * Validate with custom age limits
     */
    public void validate(LocalDate dateOfBirth, int minAge, int maxAge) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        if (dateOfBirth == null) {
            errors.add(new ValidationException.ValidationError(
                "dateOfBirth",
                "Doğum tarihi boş olamaz"
            ));
        } else {
            LocalDate now = LocalDate.now();
            
            // Check if date is in the future
            if (dateOfBirth.isAfter(now)) {
                errors.add(new ValidationException.ValidationError(
                    "dateOfBirth",
                    "Doğum tarihi gelecekte olamaz",
                    dateOfBirth
                ));
            } else {
                int age = Period.between(dateOfBirth, now).getYears();
                
                // Check minimum age
                if (age < minAge) {
                    errors.add(new ValidationException.ValidationError(
                        "dateOfBirth",
                        String.format("Yaş en az %d olmalıdır (Mevcut yaş: %d)", minAge, age),
                        dateOfBirth
                    ));
                }
                
                // Check maximum age
                if (age > maxAge) {
                    errors.add(new ValidationException.ValidationError(
                        "dateOfBirth",
                        String.format("Yaş en fazla %d olabilir (Mevcut yaş: %d)", maxAge, age),
                        dateOfBirth
                    ));
                }
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.AGE_LIMIT_EXCEEDED, errors);
        }
    }
    
    /**
     * Calculate age from date of birth
     */
    public int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
    
    /**
     * Check if age is within limits
     */
    public boolean isAgeWithinLimits(LocalDate dateOfBirth, int minAge, int maxAge) {
        if (dateOfBirth == null) {
            return false;
        }
        int age = calculateAge(dateOfBirth);
        return age >= minAge && age <= maxAge;
    }
}

