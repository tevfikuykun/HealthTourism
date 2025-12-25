package com.healthtourism.jpa.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enhanced Global Exception Handler
 * 
 * Extends ResponseEntityExceptionHandler for better Spring MVC exception handling.
 * All exceptions are caught centrally and returned in a consistent format.
 * 
 * This middleware replaces try-catch blocks in controllers, ensuring clean code.
 */
@RestControllerAdvice
public class EnhancedGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedGlobalExceptionHandler.class);
    
    /**
     * Handle MethodArgumentNotValidException (Jakarta Validation errors)
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        logger.warn("Validation failed: {}", ex.getMessage());
        
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode(ErrorCode.VALIDATION_FAILED.getCode())
            .message("Doğrulama hatası")
            .validationErrors(validationErrors)
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handle BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        logger.warn("Business exception: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode(ex.getErrorCode().getCode())
            .message(ex.getFormattedMessage())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handle ResourceNotFoundException (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .status(HttpStatus.NOT_FOUND.value())
            .errorCode(ex.getErrorCode().getCode())
            .message(ex.getFormattedMessage())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handle ValidationException
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        logger.warn("Validation exception: {}", ex.getMessage(), ex);
        
        ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder()
            .success(false)
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode(ex.getErrorCode().getCode())
            .message(ex.getFormattedMessage())
            .timestamp(LocalDateTime.now());
        
        if (ex.hasValidationErrors()) {
            Map<String, String> errors = ex.getValidationErrors().stream()
                .collect(Collectors.toMap(
                    ValidationException.ValidationError::getField,
                    ValidationException.ValidationError::getMessage,
                    (existing, replacement) -> existing
                ));
            builder.validationErrors(errors);
        }
        
        ErrorResponse error = builder.build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handle AccessDeniedException (403)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .status(HttpStatus.FORBIDDEN.value())
            .errorCode(ErrorCode.INSUFFICIENT_PERMISSIONS.getCode())
            .message("Bu işlem için yetkiniz bulunmamaktadır")
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode(ErrorCode.VALIDATION_FAILED.getCode())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handle generic Exception (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .errorCode(ErrorCode.INTERNAL_ERROR.getCode())
            .message("Beklenmeyen bir hata oluştu")
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

