package com.healthtourism.medicaldocumentservice.service;
import com.healthtourism.medicaldocumentservice.dto.MedicalDocumentDTO;
import com.healthtourism.medicaldocumentservice.entity.MedicalDocument;
import com.healthtourism.medicaldocumentservice.repository.MedicalDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MedicalDocumentService {
    @Autowired
    private MedicalDocumentRepository medicalDocumentRepository;
    
    @Autowired
    private EncryptionService encryptionService;
    
    @Value("${file.upload.dir:./uploads/medical-documents}")
    private String uploadDir;
    
    public List<MedicalDocumentDTO> getDocumentsByUser(Long userId) {
        return medicalDocumentRepository.findByUserIdAndIsActiveTrueOrderByUploadedAtDesc(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<MedicalDocumentDTO> getDocumentsByReservation(Long reservationId) {
        return medicalDocumentRepository.findByReservationId(reservationId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public MedicalDocumentDTO getDocumentById(String id) {
        MedicalDocument document = medicalDocumentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Belge bulunamadı"));
        return convertToDTO(document);
    }
    
    public MedicalDocumentDTO createDocument(MedicalDocument document) {
        document.setIsActive(true);
        document.setCreatedAt(LocalDateTime.now());
        document.setUploadedAt(LocalDateTime.now());
        return convertToDTO(medicalDocumentRepository.save(document));
    }
    
    public MedicalDocumentDTO uploadDocument(MultipartFile file, Long userId, Long reservationId, String documentType, String description) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : "";
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        String filePath = uploadPath.resolve(uniqueFilename).toString();
        
        // Save file (encrypted for GDPR/KVKK compliance)
        Path tempFile = uploadPath.resolve("temp_" + uniqueFilename);
        file.transferTo(tempFile.toFile());
        
        // Encrypt file before final storage
        Path encryptedFilePath = uploadPath.resolve(uniqueFilename);
        encryptionService.encryptFile(tempFile, encryptedFilePath);
        
        // Delete temporary unencrypted file
        Files.deleteIfExists(tempFile);
        
        filePath = encryptedFilePath.toString();
        
        // Create document entity
        MedicalDocument document = new MedicalDocument();
        document.setUserId(userId);
        document.setReservationId(reservationId);
        document.setDocumentType(documentType);
        document.setFileName(originalFilename);
        document.setFilePath(filePath);
        document.setFileSize(file.getSize());
        document.setMimeType(file.getContentType());
        document.setDescription(description);
        document.setIsActive(true);
        document.setCreatedAt(LocalDateTime.now());
        document.setUploadedAt(LocalDateTime.now());
        
        MedicalDocument saved = medicalDocumentRepository.save(document);
        return convertToDTO(saved);
    }
    
    public byte[] downloadDocument(String id) throws Exception {
        MedicalDocument document = medicalDocumentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Belge bulunamadı"));
        
        Path filePath = Paths.get(document.getFilePath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Dosya bulunamadı");
        }
        
        // Decrypt file before returning
        return encryptionService.decryptFile(filePath);
    }
    
    public void deleteDocument(String id) throws IOException {
        MedicalDocument document = medicalDocumentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Belge bulunamadı"));
        
        // Delete file from filesystem
        Path filePath = Paths.get(document.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        
        // Soft delete from database
        document.setIsActive(false);
        medicalDocumentRepository.save(document);
    }
    
    private MedicalDocumentDTO convertToDTO(MedicalDocument document) {
        MedicalDocumentDTO dto = new MedicalDocumentDTO();
        dto.setId(document.getId());
        dto.setUserId(document.getUserId());
        dto.setDoctorId(document.getDoctorId());
        dto.setReservationId(document.getReservationId());
        dto.setDocumentType(document.getDocumentType());
        dto.setFileName(document.getFileName());
        dto.setFilePath(document.getFilePath());
        dto.setFileSize(document.getFileSize());
        dto.setMimeType(document.getMimeType());
        dto.setDescription(document.getDescription());
        dto.setUploadedAt(document.getUploadedAt());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setIsActive(document.getIsActive());
        return dto;
    }
}

