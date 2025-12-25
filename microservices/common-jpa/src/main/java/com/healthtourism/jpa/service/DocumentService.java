package com.healthtourism.jpa.service;

import com.healthtourism.jpa.entity.Document;
import com.healthtourism.jpa.exception.FileValidationException;
import com.healthtourism.jpa.integration.FileStorageService;
import com.healthtourism.jpa.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Document Service - Professional Enterprise Implementation
 * 
 * Manages document metadata and file storage operations.
 * 
 * Best Practices Applied:
 * - File validation (MIME type, size, extension)
 * - Filename sanitization (UUID-based)
 * - Private file access control (presigned URLs)
 * - Soft delete
 * - Audit trail
 * - GDPR/KVKK compliance
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DocumentService {
    
    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;
    private final FileValidationService fileValidationService;
    
    /**
     * Upload and store document
     * 
     * Process:
     * 1. Validate file (MIME type, size, extension)
     * 2. Generate UUID-based file name
     * 3. Store file in storage
     * 4. Calculate file hash
     * 5. Create document metadata record
     * 
     * @param file File to upload
     * @param userId User ID (owner)
     * @param documentType Document type
     * @param isPrivate Private or public access
     * @param entityType Related entity type (optional)
     * @param entityId Related entity ID (optional)
     * @return Created Document entity
     */
    @Transactional
    public Document uploadDocument(
            MultipartFile file,
            UUID userId,
            Document.DocumentType documentType,
            Boolean isPrivate,
            String entityType,
            UUID entityId) {
        
        log.info("Uploading document: {} for user: {}", file.getOriginalFilename(), userId);
        
        // 1. Validate file
        fileValidationService.validate(file);
        
        // 2. Extract file information
        String originalFileName = file.getOriginalFilename();
        String fileExtension = fileValidationService.getFileExtension(originalFileName);
        String sanitizedFileName = fileValidationService.sanitizeFileName(originalFileName, fileExtension);
        
        // 3. Generate folder path (e.g., "users/{userId}/documents/{documentType}")
        String folderPath = generateFolderPath(userId, documentType);
        String filePath = folderPath + "/" + sanitizedFileName;
        
        // 4. Store file in storage
        String storedFilePath = fileStorageService.store(file, folderPath, sanitizedFileName);
        
        // 5. Calculate file hash (SHA-256) for integrity verification
        String fileHash = calculateFileHash(file);
        
        // 6. Create document metadata
        Document document = Document.builder()
                .originalFileName(originalFileName)
                .storedFileName(sanitizedFileName)
                .filePath(storedFilePath)
                .mimeType(file.getContentType())
                .fileExtension(fileExtension)
                .fileSize(file.getSize())
                .documentType(documentType)
                .category(documentType.name().toLowerCase().replace("_", "-"))
                .userId(userId)
                .entityType(entityType)
                .entityId(entityId != null ? entityId.toString() : null)
                .isPrivate(isPrivate != null ? isPrivate : true) // Default: private
                .storageProvider(getStorageProvider())
                .storageContainer(getStorageContainer())
                .fileHash(fileHash)
                .build();
        
        Document savedDocument = documentRepository.save(document);
        
        log.info("Document uploaded successfully: {} (ID: {})", storedFilePath, savedDocument.getId());
        
        return savedDocument;
    }
    
    /**
     * Download document
     * 
     * For private documents, generates presigned URL.
     * For public documents, returns direct access.
     * 
     * @param documentId Document ID
     * @param userId User ID (for access control)
     * @return Resource (file content) or presigned URL
     */
    @Transactional
    public DocumentAccessResult downloadDocument(UUID documentId, UUID userId) {
        log.debug("Download request for document: {} by user: {}", documentId, userId);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        
        // Access control: User must own the document or have appropriate permissions
        if (!document.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied: User does not have permission to access this document");
        }
        
        // Check if document is expired
        if (document.isExpired()) {
            log.warn("Attempted to download expired document: {}", documentId);
            throw new RuntimeException("Document has expired");
        }
        
        // For private documents, generate presigned URL
        if (document.isPrivate()) {
            String presignedUrl = fileStorageService.generatePresignedUrl(
                document.getFilePath(),
                Duration.ofMinutes(10) // 10 minutes validity
            );
            
            log.info("Generated presigned URL for private document: {} (valid for 10 minutes)", documentId);
            
            return DocumentAccessResult.builder()
                    .type(DocumentAccessResult.AccessType.PRESIGNED_URL)
                    .presignedUrl(presignedUrl)
                    .document(document)
                    .build();
        }
        
        // For public documents, return Resource
        Resource resource = fileStorageService.load(document.getFilePath());
        
        return DocumentAccessResult.builder()
                .type(DocumentAccessResult.AccessType.DIRECT)
                .resource(resource)
                .document(document)
                .build();
    }
    
    /**
     * Get presigned URL for document (for private documents)
     * 
     * @param documentId Document ID
     * @param userId User ID (for access control)
     * @param expirationMinutes URL expiration in minutes (default: 10)
     * @return Presigned URL
     */
    @Transactional(readOnly = true)
    public String getPresignedUrl(UUID documentId, UUID userId, int expirationMinutes) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        
        // Access control
        if (!document.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        if (!document.isPrivate()) {
            log.warn("Presigned URL requested for public document: {}", documentId);
            // Still generate URL for consistency
        }
        
        return fileStorageService.generatePresignedUrl(
            document.getFilePath(),
            Duration.ofMinutes(expirationMinutes)
        );
    }
    
    /**
     * Delete document (soft delete)
     * 
     * @param documentId Document ID
     * @param userId User ID (for access control)
     */
    @Transactional
    public void deleteDocument(UUID documentId, UUID userId) {
        log.info("Deleting document: {} by user: {}", documentId, userId);
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        
        // Access control
        if (!document.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        // Soft delete (file remains in storage for audit/GDPR)
        document.softDelete();
        documentRepository.save(document);
        
        // Optionally: Delete physical file after retention period
        // For healthcare data, files should be retained for legal compliance
        // fileStorageService.delete(document.getFilePath());
        
        log.info("Document soft-deleted: {}", documentId);
    }
    
    /**
     * Get documents by user ID
     * 
     * @param userId User ID
     * @return List of documents
     */
    public List<Document> getDocumentsByUserId(UUID userId) {
        return documentRepository.findByUserId(userId);
    }
    
    /**
     * Get documents by entity (reservation, appointment, etc.)
     * 
     * @param entityType Entity type
     * @param entityId Entity ID
     * @return List of documents
     */
    public List<Document> getDocumentsByEntity(String entityType, UUID entityId) {
        return documentRepository.findByEntityTypeAndEntityId(entityType, entityId.toString());
    }
    
    /**
     * Get document by ID
     * 
     * @param documentId Document ID
     * @return Document entity
     */
    public Document getDocumentById(UUID documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
    }
    
    /**
     * Generate folder path for document storage
     * 
     * @param userId User ID
     * @param documentType Document type
     * @return Folder path (e.g., "users/{userId}/documents/{documentType}")
     */
    private String generateFolderPath(UUID userId, Document.DocumentType documentType) {
        String category = documentType.name().toLowerCase().replace("_", "-");
        return String.format("users/%s/documents/%s", userId.toString(), category);
    }
    
    /**
     * Calculate file hash (SHA-256) for integrity verification
     * 
     * @param file File to hash
     * @return SHA-256 hash (hex string)
     */
    private String calculateFileHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream inputStream = file.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }
            byte[] hashBytes = digest.digest();
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Failed to calculate file hash", e);
            return null; // Hash calculation failure shouldn't block upload
        }
    }
    
    /**
     * Get storage provider name
     * 
     * @return Storage provider (e.g., "azure-blob", "aws-s3")
     */
    private String getStorageProvider() {
        // TODO: Get from configuration
        return "azure-blob";
    }
    
    /**
     * Get storage container/bucket name
     * 
     * @return Container name
     */
    private String getStorageContainer() {
        // TODO: Get from configuration
        return "healthtourism-documents";
    }
    
    /**
     * Document Access Result
     * 
     * Contains either Resource (for public files) or Presigned URL (for private files)
     */
    @lombok.Data
    @lombok.Builder
    public static class DocumentAccessResult {
        private AccessType type;
        private Resource resource; // For public files
        private String presignedUrl; // For private files
        private Document document;
        
        public enum AccessType {
            DIRECT,        // Public file - direct access
            PRESIGNED_URL  // Private file - presigned URL
        }
    }
}

