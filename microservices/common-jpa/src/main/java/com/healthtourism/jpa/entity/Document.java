package com.healthtourism.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Document Entity - Professional Enterprise Implementation
 * 
 * Stores metadata for uploaded files (medical records, passport copies, etc.)
 * Files are stored in external storage (Azure Blob, AWS S3, etc.)
 * 
 * Best Practices Applied:
 * - Extends BaseEntity for audit fields
 * - Soft delete (@SQLDelete, @Where) for data preservation
 * - Access control (public/private)
 * - File type and MIME type tracking
 * - UUID-based file naming (security)
 * - Size and metadata tracking
 * - GDPR/KVKK compliance
 */
@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_document_user_id", columnList = "user_id"),
    @Index(name = "idx_document_entity_type", columnList = "entity_type, entity_id"),
    @Index(name = "idx_document_type", columnList = "document_type"),
    @Index(name = "idx_document_is_private", columnList = "is_private"),
    @Index(name = "idx_document_file_path", columnList = "file_path"),
    @Index(name = "idx_document_deleted", columnList = "is_deleted")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE documents SET is_deleted = true, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "is_deleted = false")
@ToString(exclude = {"fileContent"}) // Exclude binary content from toString
public class Document extends BaseEntity {
    
    /**
     * Original file name (as uploaded by user)
     * For display purposes only - actual file is stored with UUID name
     */
    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;
    
    /**
     * Stored file name (UUID-based for security)
     * Example: "a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf"
     */
    @Column(name = "stored_file_name", nullable = false, unique = true, length = 255)
    private String storedFileName;
    
    /**
     * File path in storage (relative path)
     * Example: "users/123/passport/a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf"
     */
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;
    
    /**
     * File MIME type
     * Example: "application/pdf", "image/jpeg", "image/png"
     */
    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;
    
    /**
     * File extension (without dot)
     * Example: "pdf", "jpg", "png"
     */
    @Column(name = "file_extension", nullable = false, length = 10)
    private String fileExtension;
    
    /**
     * File size in bytes
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    /**
     * Document type (enum)
     * Example: PASSPORT, MEDICAL_REPORT, LAB_RESULT, PRESCRIPTION
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;
    
    /**
     * File category
     * Example: "passport", "medical-record", "lab-result", "prescription"
     */
    @Column(name = "category", length = 100)
    private String category;
    
    /**
     * Description or notes about the document
     */
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * User ID (owner of the document)
     * In microservice architecture, we use ID instead of @ManyToOne
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    /**
     * Related entity type (for flexible document association)
     * Example: "RESERVATION", "APPOINTMENT", "USER"
     */
    @Column(name = "entity_type", length = 50)
    private String entityType;
    
    /**
     * Related entity ID (UUID as string for flexibility)
     * Links document to reservation, appointment, etc.
     */
    @Column(name = "entity_id", length = 36)
    private String entityId;
    
    /**
     * Access control: Private or Public
     * Private files require presigned URL for access
     * Public files can be accessed directly
     */
    @Column(name = "is_private", nullable = false)
    @Builder.Default
    private Boolean isPrivate = true; // Default: private (healthcare data)
    
    /**
     * Storage provider
     * Example: "azure-blob", "aws-s3", "local"
     */
    @Column(name = "storage_provider", nullable = false, length = 50)
    @Builder.Default
    private String storageProvider = "azure-blob";
    
    /**
     * Storage container/bucket name
     */
    @Column(name = "storage_container", length = 100)
    private String storageContainer;
    
    /**
     * File hash (SHA-256) for integrity verification
     */
    @Column(name = "file_hash", length = 64)
    private String fileHash;
    
    /**
     * Soft delete flag (managed by @SQLDelete and @Where)
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
    
    /**
     * Document expiration date (optional)
     * For documents that expire (e.g., passport, visa)
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    /**
     * Soft delete this document
     */
    public void softDelete() {
        this.isDeleted = true;
    }
    
    /**
     * Restore a soft-deleted document
     */
    public void restore() {
        this.isDeleted = false;
    }
    
    /**
     * Check if document is expired
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
    
    /**
     * Check if document is private
     */
    public boolean isPrivate() {
        return Boolean.TRUE.equals(isPrivate);
    }
    
    @PrePersist
    protected void onCreate() {
        if (isDeleted == null) {
            isDeleted = false;
        }
        if (isPrivate == null) {
            isPrivate = true; // Default: private for healthcare data
        }
        if (storageProvider == null) {
            storageProvider = "azure-blob";
        }
    }
    
    /**
     * Document Type Enum
     */
    public enum DocumentType {
        PASSPORT("Pasaport"),
        ID_CARD("Kimlik"),
        VISA("Vize"),
        MEDICAL_REPORT("Tıbbi Rapor"),
        LAB_RESULT("Laboratuvar Sonucu"),
        PRESCRIPTION("Reçete"),
        X_RAY("Röntgen"),
        MRI("MR Görüntüsü"),
        CT_SCAN("BT Taraması"),
        ULTRASOUND("Ultrason"),
        BLOOD_TEST("Kan Tahlili"),
        INSURANCE("Sigorta"),
        CONSENT_FORM("Onam Formu"),
        OTHER("Diğer");
        
        private final String displayName;
        
        DocumentType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}

