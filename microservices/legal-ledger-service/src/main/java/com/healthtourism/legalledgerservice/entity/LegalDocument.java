package com.healthtourism.legalledgerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Legal & Ethics Ledger Document
 * Time-stamped, blockchain-backed legal documents (Informed Consent, Treatment Plans, etc.)
 */
@Entity
@Table(name = "legal_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegalDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId; // Patient ID
    
    @Column(nullable = false)
    private Long doctorId;
    
    @Column(nullable = false)
    private Long hospitalId;
    
    @Column(nullable = false)
    private Long reservationId; // Associated reservation
    
    @Column(nullable = false)
    private String documentType; // INFORMED_CONSENT, TREATMENT_PLAN, SERVICE_AGREEMENT, PROMISE_DOCUMENT
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 5000)
    private String description;
    
    // Document Content
    @Column(length = 10000)
    private String documentContent; // JSON string with document content
    
    @Column(length = 2000)
    private String documentUrl; // URL to full document (PDF, etc.)
    
    // Blockchain Integration (Time-stamped)
    @Column(nullable = false, length = 500)
    private String blockchainHash; // SHA-256 hash stored on blockchain
    
    @Column(nullable = false, length = 1000)
    private String blockchainReference; // IPFS CID
    
    @Column(nullable = false)
    private LocalDateTime timestampedAt; // Blockchain timestamp (immutable)
    
    @Column(nullable = false)
    private Boolean isBlockchainVerified;
    
    // Digital Signatures
    @Column(nullable = false)
    private Boolean patientSigned;
    
    @Column
    private LocalDateTime patientSignedAt;
    
    @Column(length = 500)
    private String patientSignatureHash; // Digital signature hash
    
    @Column(nullable = false)
    private Boolean doctorSigned;
    
    @Column
    private LocalDateTime doctorSignedAt;
    
    @Column(length = 500)
    private String doctorSignatureHash; // Digital signature hash
    
    @Column(nullable = false)
    private Boolean hospitalSigned;
    
    @Column
    private LocalDateTime hospitalSignedAt;
    
    @Column(length = 500)
    private String hospitalSignatureHash; // Digital signature hash
    
    // Status
    @Column(nullable = false)
    private String status; // DRAFT, PENDING_SIGNATURE, SIGNED, ARCHIVED
    
    // Verification
    @Column(nullable = false)
    private Boolean isVerified; // Document integrity verified
    
    @Column
    private LocalDateTime verifiedAt;
    
    // Timestamps
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (timestampedAt == null) {
            timestampedAt = LocalDateTime.now();
        }
        if (status == null || status.isEmpty()) {
            status = "DRAFT";
        }
        if (isBlockchainVerified == null) {
            isBlockchainVerified = false;
        }
        if (isVerified == null) {
            isVerified = false;
        }
        if (patientSigned == null) {
            patientSigned = false;
        }
        if (doctorSigned == null) {
            doctorSigned = false;
        }
        if (hospitalSigned == null) {
            hospitalSigned = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
