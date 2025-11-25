package com.healthtourism.medicaldocumentservice.service;
import com.healthtourism.medicaldocumentservice.dto.MedicalDocumentDTO;
import com.healthtourism.medicaldocumentservice.entity.MedicalDocument;
import com.healthtourism.medicaldocumentservice.repository.MedicalDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicalDocumentService {
    @Autowired
    private MedicalDocumentRepository medicalDocumentRepository;
    
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

