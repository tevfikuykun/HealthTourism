package com.healthtourism.medicaldocumentservice.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "medical_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDocument {
    @Id
    private String id;
    
    @Indexed
    private Long userId;
    
    @Indexed
    private Long doctorId;
    
    @Indexed
    private Long reservationId;
    
    private String documentType; // REPORT, PRESCRIPTION, IMAGE, LAB_RESULT
    private String fileName;
    private String filePath;
    private Long fileSize; // bytes
    private String mimeType;
    private String description;
    private LocalDateTime uploadedAt;
    private LocalDateTime createdAt;
    private Boolean isActive;
}

