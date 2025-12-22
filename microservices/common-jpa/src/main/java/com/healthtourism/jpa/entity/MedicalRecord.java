package com.healthtourism.jpa.entity;

import com.healthtourism.jpa.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

/**
 * Medical Record Entity with Hibernate Features
 * Critical for audit - every change tracked
 */
@Entity
@Table(name = "medical_records")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "medicalRecordCache")
@Audited
@Data
@EqualsAndHashCode(callSuper = true)
public class MedicalRecord extends AuditableEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Patient patient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Doctor doctor;
    
    @Column(name = "record_type", nullable = false)
    private String recordType; // LAB_RESULT, RADIOLOGY, PRESCRIPTION, etc.
    
    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;
    
    @Column(name = "treatment", columnDefinition = "TEXT")
    private String treatment;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "record_date", nullable = false)
    private java.time.LocalDate recordDate;
    
    @Column(name = "file_path")
    private String filePath; // IPFS hash or file path
}






