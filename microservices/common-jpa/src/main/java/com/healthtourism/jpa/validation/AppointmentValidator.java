package com.healthtourism.jpa.validation;

import com.healthtourism.jpa.exception.ErrorCode;
import com.healthtourism.jpa.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Appointment Validator
 * 
 * Validates appointment-related business rules:
 * - Appointment date cannot be in the past
 * - Patient cannot have two appointments on the same day
 * - Appointment date must be valid
 * - Check-in date validation
 */
@Component
public class AppointmentValidator {
    
    /**
     * Validate appointment date is not in the past
     */
    public void validateAppointmentDateNotInPast(LocalDateTime appointmentDate) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        if (appointmentDate == null) {
            errors.add(new ValidationException.ValidationError(
                "appointmentDate",
                "Randevu tarihi boş olamaz"
            ));
        } else if (appointmentDate.isBefore(LocalDateTime.now())) {
            errors.add(new ValidationException.ValidationError(
                "appointmentDate",
                "Randevu tarihi geçmişte olamaz",
                appointmentDate
            ));
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.APPOINTMENT_DATE_PAST, errors);
        }
    }
    
    /**
     * Validate appointment date is valid (not null, in future)
     */
    public void validateAppointmentDate(LocalDateTime appointmentDate) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        if (appointmentDate == null) {
            errors.add(new ValidationException.ValidationError(
                "appointmentDate",
                "Randevu tarihi boş olamaz"
            ));
        } else {
            LocalDateTime now = LocalDateTime.now();
            
            // Check if date is in the past
            if (appointmentDate.isBefore(now)) {
                errors.add(new ValidationException.ValidationError(
                    "appointmentDate",
                    "Randevu tarihi geçmişte olamaz",
                    appointmentDate
                ));
            }
            
            // Check if date is too far in the future (e.g., more than 1 year)
            if (appointmentDate.isAfter(now.plusYears(1))) {
                errors.add(new ValidationException.ValidationError(
                    "appointmentDate",
                    "Randevu tarihi 1 yıldan fazla ileriye alınamaz",
                    appointmentDate
                ));
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.APPOINTMENT_DATE_INVALID, errors);
        }
    }
    
    /**
     * Validate that check-in date is not before appointment date
     * Business rule: Check-in date should be on or after appointment date
     */
    public void validateCheckInDate(LocalDate checkInDate, LocalDateTime appointmentDate) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        if (checkInDate == null) {
            errors.add(new ValidationException.ValidationError(
                "checkInDate",
                "Check-in tarihi boş olamaz"
            ));
        } else if (appointmentDate != null) {
            LocalDate appointmentDateLocal = appointmentDate.toLocalDate();
            
            if (checkInDate.isBefore(appointmentDateLocal)) {
                errors.add(new ValidationException.ValidationError(
                    "checkInDate",
                    "Check-in tarihi randevu tarihinden önce olamaz",
                    checkInDate
                ));
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.APPOINTMENT_DATE_INVALID, errors);
        }
    }
    
    /**
     * Validate that patient does not have two appointments on the same day
     * This is a business rule validation that should be checked in service layer
     * after checking database
     */
    public void validateNoDoubleBookingOnSameDay(boolean hasExistingAppointmentOnDate) throws ValidationException {
        if (hasExistingAppointmentOnDate) {
            throw new ValidationException(
                ErrorCode.APPOINTMENT_DOUBLE_BOOKING,
                "Bir hasta aynı güne iki randevu alamaz"
            );
        }
    }
}

