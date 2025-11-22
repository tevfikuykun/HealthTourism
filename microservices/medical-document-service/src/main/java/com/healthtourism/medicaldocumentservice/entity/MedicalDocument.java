package com.healthtourism.medicaldocumentservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private Long doctorId;
    @Column(nullable = false) private Long reservationId;
    @Column(nullable = false) private String documentType; // REPORT, PRESCRIPTION, IMAGE, LAB_RESULT
    @Column(nullable = false) private String fileName;
    @Column(nullable = false) private String filePath;
    @Column(nullable = false) private Long fileSize; // bytes
    @Column(nullable = false) private String mimeType;
    @Column(length = 2000) private String description;
    @Column(nullable = false) private LocalDateTime uploadedAt;
    @Column(nullable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private Boolean isActive;
}

