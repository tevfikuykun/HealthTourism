package com.healthtourism.jpa.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler - Professional Enterprise Implementation
 * 
 * Centralized exception handling for all REST controllers.
 * Catches all exceptions and returns consistent ErrorResponse format.
 * 
 * Security Best Practices:
 * - Never exposes stack traces to clients
 * - Never exposes internal implementation details (table names, SQL queries, etc.)
 * - Logs technical details server-side only
 * - Returns user-friendly error messages
 * 
 * Benefits:
 * - Clean controller code (no try-catch blocks)
 * - Consistent error response format
 * - Frontend can handle errors uniformly
 * - Better security (no information leakage)
 * - Better UX (user-friendly messages)
 * - Better observability (detailed server-side logging)
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handle Business Exceptions
     * 
     * Business logic errors (e.g., "Doctor not found", "Appointment slot already taken")
     * 
     * @param ex BusinessException
     * @return ErrorResponse with 400 Bad Request status
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.error("İş mantığı hatası: {} (ErrorCode: {})", ex.getMessage(), ex.getErrorCode().getCode(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .errorCode(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle Resource Not Found Exceptions
     * 
     * @param ex ResourceNotFoundException
     * @return ErrorResponse with 404 Not Found status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Kaynak bulunamadı: {} (ErrorCode: {})", ex.getMessage(), ex.getErrorCode().getCode());
        
        ErrorResponse error = ErrorResponse.builder()
                .errorCode(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle Validation Exceptions
     * 
     * Jakarta Validation errors (e.g., @NotNull, @Email, @Size violations)
     * 
     * @param ex MethodArgumentNotValidException
     * @return ErrorResponse with 400 Bad Request status and field-specific errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validasyon hatası: {}", ex.getMessage());
        
        // Collect field-specific validation errors
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });
        
        // Create detailed error message
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("VALIDATION_FAILED")
                .message("Doğrulama hatası: " + details)
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle ValidationException (custom validation)
     * 
     * @param ex ValidationException
     * @return ErrorResponse with 400 Bad Request status
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        log.warn("Validasyon hatası: {} (ErrorCode: {})", ex.getMessage(), ex.getErrorCode().getCode());
        
        ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder()
                .errorCode(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now());
        
        // Add field errors if available
        if (ex.hasValidationErrors()) {
            Map<String, String> fieldErrors = ex.getValidationErrors().stream()
                    .collect(Collectors.toMap(
                            ValidationException.ValidationError::getField,
                            ValidationException.ValidationError::getMessage,
                            (existing, replacement) -> existing
                    ));
            builder.fieldErrors(fieldErrors);
        }
        
        ErrorResponse error = builder.build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle IllegalArgumentException
     * 
     * @param ex IllegalArgumentException
     * @return ErrorResponse with 400 Bad Request status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Geçersiz parametre: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("INVALID_ARGUMENT")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle all other exceptions (catch-all)
     * 
     * CRITICAL SECURITY: Never expose stack traces or technical details to clients!
     * Log technical details server-side only.
     * 
     * @param ex Exception
     * @return ErrorResponse with 500 Internal Server Error status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        // Log full stack trace server-side only (for debugging)
        log.error("Beklenmedik hata oluştu: ", ex);
        
        // Return user-friendly message (no technical details)
        ErrorResponse error = ErrorResponse.internalServerError();
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
