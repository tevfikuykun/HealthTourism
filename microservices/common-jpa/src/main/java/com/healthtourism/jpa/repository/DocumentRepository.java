package com.healthtourism.jpa.repository;

import com.healthtourism.jpa.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Document Repository
 * 
 * Professional repository with:
 * - Pagination support
 * - Soft delete filtering (isDeleted = false) via @Where annotation on Entity
 * - Custom queries for business needs
 * 
 * Note: @Where annotation on Document entity automatically filters deleted documents,
 * so all queries implicitly exclude deleted documents.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    
    /**
     * Find documents by user ID
     */
    List<Document> findByUserId(UUID userId);
    
    /**
     * Find documents by user ID with pagination
     */
    Page<Document> findByUserId(UUID userId, Pageable pageable);
    
    /**
     * Find documents by entity type and entity ID
     * Example: Find all documents for a reservation
     */
    List<Document> findByEntityTypeAndEntityId(String entityType, String entityId);
    
    /**
     * Find documents by document type
     */
    List<Document> findByDocumentType(Document.DocumentType documentType);
    
    /**
     * Find documents by document type and user ID
     */
    List<Document> findByDocumentTypeAndUserId(Document.DocumentType documentType, UUID userId);
    
    /**
     * Find documents by file path
     */
    Optional<Document> findByFilePath(String filePath);
    
    /**
     * Count documents by user ID
     */
    long countByUserId(UUID userId);
    
    /**
     * Count documents by document type
     */
    long countByDocumentType(Document.DocumentType documentType);
}

