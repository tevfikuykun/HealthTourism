package com.healthtourism.jpa.validation;

import com.healthtourism.jpa.exception.ErrorCode;
import com.healthtourism.jpa.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Phone Number Validator
 * 
 * Validates phone numbers according to international formats.
 * Supports various phone number formats from different countries.
 */
@Component
public class PhoneValidator implements BusinessRuleValidator<String> {
    
    // International phone number pattern
    // Supports: +90 555 123 4567, +1 (555) 123-4567, etc.
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$"
    );
    
    // Turkish phone number pattern (specific validation)
    private static final Pattern TURKISH_PHONE_PATTERN = Pattern.compile(
        "^(\\+90|0)?[5][0-9]{9}$"
    );
    
    private static final int MIN_PHONE_LENGTH = 10;
    private static final int MAX_PHONE_LENGTH = 15;
    
    @Override
    public void validate(String phone) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        if (phone == null || phone.trim().isEmpty()) {
            errors.add(new ValidationException.ValidationError(
                "phone",
                "Telefon numarası boş olamaz"
            ));
        } else {
            String cleanedPhone = phone.trim().replaceAll("[\\s\\-()]", "");
            
            // Length validation
            if (cleanedPhone.length() < MIN_PHONE_LENGTH || cleanedPhone.length() > MAX_PHONE_LENGTH) {
                errors.add(new ValidationException.ValidationError(
                    "phone",
                    String.format("Telefon numarası %d-%d karakter arasında olmalıdır", 
                        MIN_PHONE_LENGTH, MAX_PHONE_LENGTH),
                    phone
                ));
            }
            
            // Format validation
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                errors.add(new ValidationException.ValidationError(
                    "phone",
                    "Telefon numarası formatı geçersiz. Örnek: +90 555 123 4567",
                    phone
                ));
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.PHONE_FORMAT_INVALID, errors);
        }
    }
    
    /**
     * Validate Turkish phone number specifically
     */
    public void validateTurkish(String phone) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        if (phone == null || phone.trim().isEmpty()) {
            errors.add(new ValidationException.ValidationError(
                "phone",
                "Telefon numarası boş olamaz"
            ));
        } else if (!TURKISH_PHONE_PATTERN.matcher(phone).matches()) {
            errors.add(new ValidationException.ValidationError(
                "phone",
                "Türk telefon numarası formatı geçersiz. Örnek: +90 555 123 4567 veya 0555 123 4567",
                phone
            ));
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.PHONE_FORMAT_INVALID, errors);
        }
    }
}

