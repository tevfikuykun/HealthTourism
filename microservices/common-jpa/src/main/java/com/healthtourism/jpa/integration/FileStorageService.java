package com.healthtourism.jpa.integration;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * File Storage Service Interface
 * 
 * Abstraction for file storage operations.
 * Supports multiple storage providers (Azure Blob, AWS S3, Local).
 * 
 * Best Practices:
 * - Generic interface for storage provider abstraction
 * - Presigned URL support for private files
 * - Metadata support
 * - Error handling
 */
public interface FileStorageService {
    
    /**
     * Store file in storage
     * 
     * @param file MultipartFile to store
     * @param folderPath Folder path (e.g., "users/123/passport")
     * @return Stored file path (relative path)
     * @throws FileStorageException if storage fails
     */
    String store(MultipartFile file, String folderPath);
    
    /**
     * Store file with custom file name
     * 
     * @param file MultipartFile to store
     * @param folderPath Folder path
     * @param fileName Custom file name (will be sanitized)
     * @return Stored file path
     */
    String store(MultipartFile file, String folderPath, String fileName);
    
    /**
     * Store file from InputStream
     * 
     * @param inputStream File input stream
     * @param folderPath Folder path
     * @param fileName File name
     * @param contentType Content type (MIME type)
     * @param contentLength Content length in bytes
     * @return Stored file path
     */
    String store(InputStream inputStream, String folderPath, String fileName, 
                 String contentType, long contentLength);
    
    /**
     * Load file as Resource
     * 
     * @param filePath File path (relative path)
     * @return Resource (file content)
     * @throws FileNotFoundException if file doesn't exist
     */
    Resource load(String filePath);
    
    /**
     * Load file as InputStream
     * 
     * @param filePath File path
     * @return InputStream
     */
    InputStream loadAsInputStream(String filePath);
    
    /**
     * Delete file from storage
     * 
     * @param filePath File path to delete
     * @return true if deletion successful
     */
    boolean delete(String filePath);
    
    /**
     * Check if file exists
     * 
     * @param filePath File path
     * @return true if file exists
     */
    boolean exists(String filePath);
    
    /**
     * Get file metadata
     * 
     * @param filePath File path
     * @return File metadata (size, content type, last modified, etc.)
     */
    Optional<FileMetadata> getMetadata(String filePath);
    
    /**
     * Generate presigned URL for private file access
     * 
     * Presigned URLs are time-limited (typically 5-10 minutes) and allow
     * temporary access to private files without authentication.
     * 
     * @param filePath File path
     * @param expiration Duration until URL expires (e.g., Duration.ofMinutes(10))
     * @return Presigned URL
     */
    String generatePresignedUrl(String filePath, Duration expiration);
    
    /**
     * Generate presigned URL with default expiration (10 minutes)
     * 
     * @param filePath File path
     * @return Presigned URL (valid for 10 minutes)
     */
    default String generatePresignedUrl(String filePath) {
        return generatePresignedUrl(filePath, Duration.ofMinutes(10));
    }
    
    /**
     * Copy file to another location
     * 
     * @param sourcePath Source file path
     * @param destinationPath Destination file path
     * @return Destination file path
     */
    String copy(String sourcePath, String destinationPath);
    
    /**
     * Move file to another location
     * 
     * @param sourcePath Source file path
     * @param destinationPath Destination file path
     * @return Destination file path
     */
    String move(String sourcePath, String destinationPath);
    
    /**
     * Get file size in bytes
     * 
     * @param filePath File path
     * @return File size in bytes
     */
    long getFileSize(String filePath);
    
    /**
     * Get file content type (MIME type)
     * 
     * @param filePath File path
     * @return Content type
     */
    String getContentType(String filePath);
    
    /**
     * File Metadata
     */
    class FileMetadata {
        private final String fileName;
        private final long size;
        private final String contentType;
        private final java.time.LocalDateTime lastModified;
        private final Map<String, String> additionalMetadata;
        
        public FileMetadata(String fileName, long size, String contentType, 
                           java.time.LocalDateTime lastModified, Map<String, String> additionalMetadata) {
            this.fileName = fileName;
            this.size = size;
            this.contentType = contentType;
            this.lastModified = lastModified;
            this.additionalMetadata = additionalMetadata != null ? 
                Map.copyOf(additionalMetadata) : Map.of();
        }
        
        public String getFileName() { return fileName; }
        public long getSize() { return size; }
        public String getContentType() { return contentType; }
        public java.time.LocalDateTime getLastModified() { return lastModified; }
        public Map<String, String> getAdditionalMetadata() { return additionalMetadata; }
    }
    
    /**
     * File Storage Exception
     */
    class FileStorageException extends RuntimeException {
        public FileStorageException(String message) {
            super(message);
        }
        
        public FileStorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * File Not Found Exception
     */
    class FileNotFoundException extends FileStorageException {
        public FileNotFoundException(String filePath) {
            super("File not found: " + filePath);
        }
    }
}
