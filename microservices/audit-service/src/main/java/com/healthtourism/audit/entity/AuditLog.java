package com.healthtourism.audit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * HIPAA/KVKK Compliant Audit Log Entity
 * Tracks all access to PHI (Personal Health Information)
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_resource_type", columnList = "resourceType"),
    @Index(name = "idx_action", columnList = "action"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId; // Who accessed
    
    @Column(nullable = false)
    private String userEmail;
    
    @Column(nullable = false)
    private String userRole;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;
    
    @Column(nullable = false)
    private String resourceId;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Action action;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(length = 500)
    private String ipAddress;
    
    @Column(length = 500)
    private String userAgent;
    
    @Column(length = 2000)
    private String description;
    
    @Column(nullable = false)
    private Boolean success;
    
    @Column(length = 1000)
    private String errorMessage;
    
    @Column(length = 500)
    private String sessionId;
    
    public enum ResourceType {
        MEDICAL_DOCUMENT,
        PATIENT_RECORD,
        HEALTH_RECORD,
        PAYMENT,
        RESERVATION,
        QUOTE,
        LEAD,
        USER_PROFILE,
        OTHER
    }
    
    public enum Action {
        CREATE,
        READ,
        UPDATE,
        DELETE,
        DOWNLOAD,
        UPLOAD,
        EXPORT,
        ACCESS,
        LOGIN,
        LOGOUT
    }
}
