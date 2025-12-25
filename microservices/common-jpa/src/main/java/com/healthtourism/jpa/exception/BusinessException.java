package com.healthtourism.jpa.exception;

/**
 * Business Exception
 * 
 * Base exception class for all business rule violations.
 * Should be used instead of generic RuntimeException or IllegalArgumentException.
 * 
 * Features:
 * - Error codes for consistent error handling
 * - Turkish and English message support
 * - Structured error information
 * - Proper HTTP status mapping
 */
public class BusinessException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final Object[] messageArgs;
    
    /**
     * Create business exception with error code
     * 
     * @param errorCode Error code enum
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.messageArgs = null;
    }
    
    /**
     * Create business exception with error code and custom message
     * 
     * @param errorCode Error code enum
     * @param message Custom message
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.messageArgs = null;
    }
    
    /**
     * Create business exception with error code and message arguments
     * 
     * @param errorCode Error code enum
     * @param messageArgs Message arguments for formatting
     */
    public BusinessException(ErrorCode errorCode, Object... messageArgs) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }
    
    /**
     * Create business exception with error code, custom message, and cause
     * 
     * @param errorCode Error code enum
     * @param message Custom message
     * @param cause Original exception
     */
    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.messageArgs = null;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public Object[] getMessageArgs() {
        return messageArgs;
    }
    
    /**
     * Get formatted error message
     */
    public String getFormattedMessage() {
        if (messageArgs != null && messageArgs.length > 0) {
            return String.format(errorCode.getMessage(), messageArgs);
        }
        return getMessage();
    }
}

