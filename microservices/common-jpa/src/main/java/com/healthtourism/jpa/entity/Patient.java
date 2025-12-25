package com.healthtourism.jpa.entity;

import com.healthtourism.jpa.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

/**
 * Patient Entity with Hibernate Features
 * - L2 Cache (Redis)
 * - Dirty Checking
 * - Envers Audit
 * - Indexed fields for performance (email, national_id, passport_number, phone)
 */
@Entity
@Table(
    name = "patients",
    indexes = {
        @Index(name = "idx_patients_email", columnList = "email", unique = true),
        @Index(name = "idx_patients_national_id", columnList = "national_id"),
        @Index(name = "idx_patients_passport_number", columnList = "passport_number"),
        @Index(name = "idx_patients_phone", columnList = "phone"),
        @Index(name = "idx_patients_email_national_id", columnList = "email,national_id")
    }
)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "patientCache")
@Audited
@Data
@EqualsAndHashCode(callSuper = true)
public class Patient extends AuditableEntity {
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(name = "date_of_birth")
    private java.time.LocalDate dateOfBirth;
    
    @Column(name = "national_id")
    private String nationalId; // Encrypted field
    
    @Column(name = "passport_number")
    private String passportNumber; // Encrypted field
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(name = "blood_type")
    private String bloodType;
    
    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;
    
    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;
    
    // Lazy loading for related entities
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private java.util.List<Reservation> reservations;
    
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private java.util.List<MedicalRecord> medicalRecords;
    
    public enum Gender {
        MALE, FEMALE, OTHER
    }
}








