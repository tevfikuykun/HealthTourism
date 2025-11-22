package com.healthtourism.filestorageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataDTO {
    private Long id;
    private String fileName;
    private String originalFileName;
    private String filePath;
    private String contentType;
    private Long fileSize;
    private String uploadedBy;
    private String serviceName;
    private String category;
    private LocalDateTime uploadedAt;
}

