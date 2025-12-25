package com.healthtourism.jpa.service;

import com.healthtourism.jpa.exception.FileValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * File Validation Service
 * 
 * Validates uploaded files for security and compliance.
 * 
 * Validations:
 * - MIME type whitelist
 * - File size limit
 * - Filename sanitization
 * - File extension validation
 */
@Service
@Slf4j
public class FileValidationService {
    
    // Allowed MIME types (security whitelist)
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
        // Documents
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
        
        // Images
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/gif",
        "image/webp",
        "image/bmp",
        "image/tiff",
        
        // Medical images
        "image/dicom", // DICOM medical imaging
        
        // Text
        "text/plain",
        "text/csv"
    );
    
    // Allowed file extensions
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        "pdf", "doc", "docx", "xls", "xlsx",
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "tiff",
        "txt", "csv", "dcm" // DICOM
    );
    
    // Dangerous file extensions (blocked)
    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
        "exe", "bat", "cmd", "com", "pif", "scr", "vbs", "js", "jar",
        "php", "asp", "aspx", "jsp", "sh", "py", "rb", "pl"
    );
    
    // Default file size limit: 10 MB
    private static final long DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB in bytes
    
    // Maximum file size: 50 MB (for large medical images)
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB
    
    /**
     * Validate uploaded file
     * 
     * @param file MultipartFile to validate
     * @throws FileValidationException if validation fails
     */
    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("File is empty or null");
        }
        
        validateFileName(file.getOriginalFilename());
        validateFileSize(file.getSize());
        validateMimeType(file.getContentType());
        validateFileExtension(file.getOriginalFilename());
    }
    
    /**
     * Validate file name
     * 
     * @param fileName Original file name
     * @throws FileValidationException if file name is invalid
     */
    public void validateFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new FileValidationException("File name cannot be empty");
        }
        
        // Check for path traversal attempts
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new FileValidationException("Invalid file name: path traversal detected");
        }
        
        // Check length
        if (fileName.length() > 255) {
            throw new FileValidationException("File name too long (max 255 characters)");
        }
    }
    
    /**
     * Validate file size
     * 
     * @param fileSize File size in bytes
     * @throws FileValidationException if file size exceeds limit
     */
    public void validateFileSize(long fileSize) {
        if (fileSize <= 0) {
            throw new FileValidationException("File size must be greater than 0");
        }
        
        if (fileSize > MAX_FILE_SIZE) {
            throw new FileValidationException(
                String.format("File size (%d bytes) exceeds maximum allowed size (%d bytes / %d MB)",
                    fileSize, MAX_FILE_SIZE, MAX_FILE_SIZE / (1024 * 1024))
            );
        }
        
        // Warn if file is large
        if (fileSize > DEFAULT_MAX_FILE_SIZE) {
            log.warn("Large file detected: {} bytes ({} MB)", fileSize, fileSize / (1024 * 1024));
        }
    }
    
    /**
     * Validate MIME type
     * 
     * @param mimeType MIME type to validate
     * @throws FileValidationException if MIME type is not allowed
     */
    public void validateMimeType(String mimeType) {
        if (mimeType == null || mimeType.trim().isEmpty()) {
            throw new FileValidationException("MIME type cannot be empty");
        }
        
        // Normalize MIME type (remove charset, etc.)
        String normalizedMimeType = mimeType.split(";")[0].trim().toLowerCase();
        
        if (!ALLOWED_MIME_TYPES.contains(normalizedMimeType)) {
            throw new FileValidationException(
                String.format("MIME type '%s' is not allowed. Allowed types: %s",
                    mimeType, String.join(", ", ALLOWED_MIME_TYPES))
            );
        }
    }
    
    /**
     * Validate file extension
     * 
     * @param fileName File name with extension
     * @throws FileValidationException if extension is blocked or invalid
     */
    public void validateFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new FileValidationException("File must have an extension");
        }
        
        String extension = getFileExtension(fileName).toLowerCase();
        
        // Check if extension is blocked (dangerous)
        if (BLOCKED_EXTENSIONS.contains(extension)) {
            throw new FileValidationException(
                String.format("File extension '%s' is blocked for security reasons", extension)
            );
        }
        
        // Check if extension is in whitelist
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileValidationException(
                String.format("File extension '%s' is not allowed. Allowed extensions: %s",
                    extension, String.join(", ", ALLOWED_EXTENSIONS))
            );
        }
    }
    
    /**
     * Extract file extension from file name
     * 
     * @param fileName File name
     * @return File extension (without dot)
     */
    public String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastDotIndex + 1);
    }
    
    /**
     * Sanitize file name (remove special characters, generate UUID-based name)
     * 
     * @param originalFileName Original file name
     * @param extension File extension
     * @return Sanitized file name (UUID-based)
     */
    public String sanitizeFileName(String originalFileName, String extension) {
        // Generate UUID-based file name for security
        // Format: UUID.extension
        // Example: "a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf"
        String uuid = java.util.UUID.randomUUID().toString();
        return uuid + "." + extension;
    }
    
    /**
     * Get allowed MIME types
     * 
     * @return Set of allowed MIME types
     */
    public Set<String> getAllowedMimeTypes() {
        return Set.copyOf(ALLOWED_MIME_TYPES);
    }
    
    /**
     * Get allowed file extensions
     * 
     * @return Set of allowed extensions
     */
    public Set<String> getAllowedExtensions() {
        return Set.copyOf(ALLOWED_EXTENSIONS);
    }
    
    /**
     * Get maximum file size
     * 
     * @return Maximum file size in bytes
     */
    public long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }
    
    /**
     * Get default file size limit
     * 
     * @return Default file size limit in bytes
     */
    public long getDefaultMaxFileSize() {
        return DEFAULT_MAX_FILE_SIZE;
    }
}

