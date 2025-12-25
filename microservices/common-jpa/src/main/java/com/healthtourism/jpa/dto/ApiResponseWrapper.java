package com.healthtourism.jpa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * API Response Wrapper
 * 
 * Standard response format for all API endpoints.
 * Ensures consistent response structure across the application.
 * 
 * Frontend teams can always expect the same response structure:
 * {
 *   "success": true,
 *   "message": "İşlem başarılı",
 *   "data": { ... },
 *   "timestamp": "2025-12-25T10:00:00"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseWrapper<T> implements Serializable {
    
    /**
     * Operation success status
     */
    private Boolean success;
    
    /**
     * Human-readable message
     */
    private String message;
    
    /**
     * Response data (can be any type: Object, List, etc.)
     */
    private T data;
    
    /**
     * Timestamp when response was generated
     */
    private LocalDateTime timestamp;
    
    /**
     * Create successful response with data
     * 
     * @param message Success message
     * @param data Response data
     * @param <T> Data type
     * @return ApiResponseWrapper
     */
    public static <T> ApiResponseWrapper<T> success(String message, T data) {
        return ApiResponseWrapper.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    /**
     * Create successful response without data
     * 
     * @param message Success message
     * @param <T> Data type
     * @return ApiResponseWrapper
     */
    public static <T> ApiResponseWrapper<T> success(String message) {
        return ApiResponseWrapper.<T>builder()
            .success(true)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    /**
     * Create successful response with default message
     * 
     * @param data Response data
     * @param <T> Data type
     * @return ApiResponseWrapper
     */
    public static <T> ApiResponseWrapper<T> success(T data) {
        return success("İşlem başarılı", data);
    }
    
    /**
     * Create error response
     * 
     * @param message Error message
     * @param <T> Data type
     * @return ApiResponseWrapper
     */
    public static <T> ApiResponseWrapper<T> error(String message) {
        return ApiResponseWrapper.<T>builder()
            .success(false)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    /**
     * Create error response with data
     * 
     * @param message Error message
     * @param data Error data
     * @param <T> Data type
     * @return ApiResponseWrapper
     */
    public static <T> ApiResponseWrapper<T> error(String message, T data) {
        return ApiResponseWrapper.<T>builder()
            .success(false)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }
}

