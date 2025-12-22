package com.healthtourism.userservice.entity;

import com.healthtourism.security.encryption.EncryptedFieldConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Sensitive Data Entity
 * All sensitive fields are encrypted with AES-256-GCM
 */
@Entity
@Table(name = "user_sensitive_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSensitiveData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    // TC Kimlik - Encrypted
    @Column(name = "tc_kimlik")
    @Convert(converter = EncryptedFieldConverter.class)
    private String tcKimlik;

    // Passport Number - Encrypted
    @Column(name = "passport_number")
    @Convert(converter = EncryptedFieldConverter.class)
    private String passportNumber;

    // Medical Conditions - Encrypted
    @Column(name = "medical_conditions", columnDefinition = "TEXT")
    @Convert(converter = EncryptedFieldConverter.class)
    private String medicalConditions;

    // Allergies - Encrypted
    @Column(name = "allergies", columnDefinition = "TEXT")
    @Convert(converter = EncryptedFieldConverter.class)
    private String allergies;

    // Blood Type - Encrypted
    @Column(name = "blood_type")
    @Convert(converter = EncryptedFieldConverter.class)
    private String bloodType;

    // Emergency Contact - Encrypted
    @Column(name = "emergency_contact")
    @Convert(converter = EncryptedFieldConverter.class)
    private String emergencyContact;

    // Insurance Info - Encrypted
    @Column(name = "insurance_info", columnDefinition = "TEXT")
    @Convert(converter = EncryptedFieldConverter.class)
    private String insuranceInfo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}








