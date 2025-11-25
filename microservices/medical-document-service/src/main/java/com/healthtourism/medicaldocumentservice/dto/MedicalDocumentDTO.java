package com.healthtourism.medicaldocumentservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDocumentDTO {
    private String id;
    private Long userId;
    private Long doctorId;
    private Long reservationId;
    private String documentType;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private String description;
    private LocalDateTime uploadedAt;
    private LocalDateTime createdAt;
    private Boolean isActive;
}

