package com.healthtourism.jpa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API Response Wrapper
 * 
 * Standardized API response format for all endpoints.
 * Provides consistent structure: {success, message, data, timestamp}
 * 
 * Usage:
 * return ResponseEntity.ok(ApiResponse.success("Operation successful", data));
 * return ResponseEntity.badRequest(ApiResponse.error("Operation failed"));
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON
public class ApiResponse<T> {
    
    /**
     * Operation success status
     */
    private Boolean success;
    
    /**
     * Human-readable message
     */
    private String message;
    
    /**
     * Response data (generic type)
     */
    private T data;
    
    /**
     * Error details (only for error responses)
     */
    private ErrorDetails error;
    
    /**
     * Response timestamp
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Create success response with data
     * 
     * @param message Success message
     * @param data Response data
     * @return ApiResponse with success = true
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create success response without data
     * 
     * @param message Success message
     * @return ApiResponse with success = true
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create error response
     * 
     * @param message Error message
     * @return ApiResponse with success = false
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create error response with error details
     * 
     * @param message Error message
     * @param errorDetails Error details
     * @return ApiResponse with success = false and error details
     */
    public static <T> ApiResponse<T> error(String message, ErrorDetails errorDetails) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Error Details
     * Contains detailed error information for debugging
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        /**
         * Error code (for frontend i18n)
         */
        private String code;
        
        /**
         * Error message
         */
        private String message;
        
        /**
         * Field-specific validation errors
         */
        private java.util.Map<String, String> fieldErrors;
        
        /**
         * Stack trace (only in development mode)
         */
        private String stackTrace;
    }
}

