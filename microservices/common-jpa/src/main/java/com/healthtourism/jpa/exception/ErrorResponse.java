package com.healthtourism.jpa.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard Error Response
 * 
 * Unified error response format for all API endpoints.
 * Used by GlobalExceptionHandler to return consistent error responses.
 * 
 * Security: Never exposes stack traces or internal implementation details to clients.
 * 
 * Format:
 * {
 *   "errorCode": "RESERVATION_CONFLICT",
 *   "message": "Doktor bu saatte müsait değil",
 *   "timestamp": "2024-03-25T10:30:00"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON
public class ErrorResponse {
    
    /**
     * Error code (for frontend i18n and error handling)
     * Examples: "RESERVATION_CONFLICT", "VALIDATION_FAILED", "RESOURCE_NOT_FOUND"
     */
    private String errorCode;
    
    /**
     * Human-readable error message (for display to end users)
     * Should NOT contain technical details (stack traces, table names, etc.)
     */
    private String message;
    
    /**
     * Timestamp when error occurred
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Field-specific validation errors (only for validation failures)
     * Example: {"email": "Email formatı geçersiz", "password": "Şifre boş olamaz"}
     */
    private Map<String, String> fieldErrors;
    
    /**
     * Create error response with error code and message
     * 
     * @param errorCode Error code
     * @param message Error message
     * @return ErrorResponse instance
     */
    public static ErrorResponse of(String errorCode, String message) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create error response with field errors (for validation failures)
     * 
     * @param errorCode Error code
     * @param message Error message
     * @param fieldErrors Field-specific validation errors
     * @return ErrorResponse instance
     */
    public static ErrorResponse of(String errorCode, String message, Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create internal server error response
     * 
     * @return ErrorResponse with generic error message
     */
    public static ErrorResponse internalServerError() {
        return ErrorResponse.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("Şu an işleminizi gerçekleştiremiyoruz, lütfen daha sonra tekrar deneyiniz.")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
