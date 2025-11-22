package com.healthtourism.telemedicineservice.service;
import com.healthtourism.telemedicineservice.dto.TelemedicineConsultationDTO;
import com.healthtourism.telemedicineservice.entity.TelemedicineConsultation;
import com.healthtourism.telemedicineservice.repository.TelemedicineConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TelemedicineConsultationService {
    @Autowired
    private TelemedicineConsultationRepository telemedicineConsultationRepository;
    
    @Transactional
    public TelemedicineConsultationDTO createConsultation(TelemedicineConsultation consultation) {
        LocalDateTime endTime = consultation.getConsultationDate().plusMinutes(consultation.getDurationMinutes());
        List<TelemedicineConsultation> conflicting = telemedicineConsultationRepository.findConflictingConsultations(
                consultation.getDoctorId(), consultation.getConsultationDate(), endTime);
        if (!conflicting.isEmpty()) {
            throw new RuntimeException("Bu saatte başka bir konsültasyon var");
        }
        
        consultation.setStatus("SCHEDULED");
        consultation.setMeetingLink("https://meet.healthtourism.com/" + UUID.randomUUID().toString());
        consultation.setCreatedAt(LocalDateTime.now());
        return convertToDTO(telemedicineConsultationRepository.save(consultation));
    }
    
    public List<TelemedicineConsultationDTO> getConsultationsByUser(Long userId) {
        return telemedicineConsultationRepository.findByUserIdOrderByConsultationDateDesc(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public TelemedicineConsultationDTO getConsultationById(Long id) {
        TelemedicineConsultation consultation = telemedicineConsultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Konsültasyon bulunamadı"));
        return convertToDTO(consultation);
    }
    
    @Transactional
    public TelemedicineConsultationDTO updateStatus(Long id, String status) {
        TelemedicineConsultation consultation = telemedicineConsultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Konsültasyon bulunamadı"));
        consultation.setStatus(status);
        return convertToDTO(telemedicineConsultationRepository.save(consultation));
    }
    
    private TelemedicineConsultationDTO convertToDTO(TelemedicineConsultation consultation) {
        TelemedicineConsultationDTO dto = new TelemedicineConsultationDTO();
        dto.setId(consultation.getId());
        dto.setUserId(consultation.getUserId());
        dto.setDoctorId(consultation.getDoctorId());
        dto.setConsultationDate(consultation.getConsultationDate());
        dto.setDurationMinutes(consultation.getDurationMinutes());
        dto.setConsultationType(consultation.getConsultationType());
        dto.setStatus(consultation.getStatus());
        dto.setFee(consultation.getFee());
        dto.setNotes(consultation.getNotes());
        dto.setMeetingLink(consultation.getMeetingLink());
        dto.setCreatedAt(consultation.getCreatedAt());
        return dto;
    }
}

