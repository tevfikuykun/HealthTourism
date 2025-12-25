package com.healthtourism.jpa.util;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.regex.Pattern;

/**
 * Input Sanitizer
 * 
 * Utility for sanitizing user input to prevent XSS and SQL Injection attacks.
 * 
 * Security Best Practices:
 * - HTML escaping for XSS prevention
 * - SQL injection pattern detection
 * - Path traversal prevention
 * - Script tag removal
 */
@Component
public class InputSanitizer {
    
    // SQL Injection patterns (common attack vectors)
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(.*)(\\b(OR|AND|UNION|SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|EXECUTE)\\b)(.*)",
        Pattern.CASE_INSENSITIVE
    );
    
    // Path traversal patterns
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
        ".*(\\.\\./|\\.\\.\\\\).*"
    );
    
    // Script tag patterns
    private static final Pattern SCRIPT_TAG_PATTERN = Pattern.compile(
        "(?i)<script[^>]*>.*?</script>",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    
    /**
     * Sanitize string input (HTML escape + pattern detection)
     * 
     * @param input Raw input string
     * @return Sanitized string
     */
    public String sanitize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        // 1. HTML escape (XSS prevention)
        String sanitized = HtmlUtils.htmlEscape(input);
        
        // 2. Remove script tags
        sanitized = SCRIPT_TAG_PATTERN.matcher(sanitized).replaceAll("");
        
        // 3. Trim whitespace
        sanitized = sanitized.trim();
        
        return sanitized;
    }
    
    /**
     * Sanitize string input without HTML escaping (for non-HTML contexts)
     * 
     * @param input Raw input string
     * @return Sanitized string
     */
    public String sanitizeWithoutHtmlEscape(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        // Remove script tags
        String sanitized = SCRIPT_TAG_PATTERN.matcher(input).replaceAll("");
        
        // Trim whitespace
        sanitized = sanitized.trim();
        
        return sanitized;
    }
    
    /**
     * Check if input contains SQL injection patterns
     * 
     * @param input Input string
     * @return true if SQL injection pattern detected
     */
    public boolean containsSqlInjection(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        return SQL_INJECTION_PATTERN.matcher(input).matches();
    }
    
    /**
     * Check if input contains path traversal patterns
     * 
     * @param input Input string
     * @return true if path traversal pattern detected
     */
    public boolean containsPathTraversal(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        return PATH_TRAVERSAL_PATTERN.matcher(input).matches();
    }
    
    /**
     * Validate and sanitize input (throws exception if malicious pattern detected)
     * 
     * @param input Input string
     * @return Sanitized string
     * @throws IllegalArgumentException if malicious pattern detected
     */
    public String validateAndSanitize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        // Check for SQL injection
        if (containsSqlInjection(input)) {
            throw new IllegalArgumentException("Geçersiz karakterler tespit edildi");
        }
        
        // Check for path traversal
        if (containsPathTraversal(input)) {
            throw new IllegalArgumentException("Geçersiz yol formatı tespit edildi");
        }
        
        // Sanitize
        return sanitize(input);
    }
    
    /**
     * Sanitize file name (for file uploads)
     * 
     * @param fileName Original file name
     * @return Sanitized file name
     */
    public String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return fileName;
        }
        
        // Remove path traversal
        String sanitized = fileName.replaceAll("(\\.\\./|\\.\\.\\\\)", "");
        
        // Remove special characters (keep alphanumeric, dots, hyphens, underscores)
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        // Limit length
        if (sanitized.length() > 255) {
            sanitized = sanitized.substring(0, 255);
        }
        
        return sanitized;
    }
}

