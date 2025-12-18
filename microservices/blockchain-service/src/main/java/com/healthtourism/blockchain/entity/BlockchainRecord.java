package com.healthtourism.blockchain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Blockchain Record Entity
 * Stores immutable records of medical treatments and payments
 */
@Entity
@Table(name = "blockchain_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String blockHash; // SHA-256 hash
    
    @Column(nullable = false)
    private String previousHash; // Previous block hash
    
    @Column(nullable = false)
    private Long blockIndex;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecordType recordType;
    
    @Column(nullable = false)
    private String recordId; // Original record ID (e.g., reservation ID, payment ID)
    
    @Column(nullable = false)
    private String dataHash; // SHA-256 hash of the record data (off-chain storage)
    
    @Column(length = 500)
    private String dataReference; // Reference to off-chain storage (e.g., S3 URL, IPFS hash)
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    private String userId; // User who owns this record
    
    @Column(nullable = false)
    private String signature; // Digital signature
    
    @Column(nullable = false)
    private Boolean isValid;
    
    @Column(length = 1000)
    private String metadata; // Optional metadata (JSON string) - lightweight data only
    
    public enum RecordType {
        MEDICAL_TREATMENT,
        PAYMENT,
        RESERVATION,
        CONSULTATION,
        DOCUMENT_ACCESS,
        AUDIT_LOG
    }
}
