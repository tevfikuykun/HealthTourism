package com.healthtourism.patientfollowupservice.service;
import com.healthtourism.patientfollowupservice.dto.PatientFollowUpDTO;
import com.healthtourism.patientfollowupservice.entity.PatientFollowUp;
import com.healthtourism.patientfollowupservice.repository.PatientFollowUpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientFollowUpService {
    @Autowired
    private PatientFollowUpRepository patientFollowUpRepository;
    
    public List<PatientFollowUpDTO> getFollowUpsByUser(Long userId) {
        return patientFollowUpRepository.findByUserIdOrderByFollowUpDateDesc(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public PatientFollowUpDTO getFollowUpById(Long id) {
        PatientFollowUp followUp = patientFollowUpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Takip kaydı bulunamadı"));
        return convertToDTO(followUp);
    }
    
    public PatientFollowUpDTO createFollowUp(PatientFollowUp followUp) {
        followUp.setStatus("SCHEDULED");
        followUp.setCreatedAt(LocalDateTime.now());
        return convertToDTO(patientFollowUpRepository.save(followUp));
    }
    
    @Transactional
    public PatientFollowUpDTO updateFollowUp(Long id, String recoveryProgress, String notes) {
        PatientFollowUp followUp = patientFollowUpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Takip kaydı bulunamadı"));
        followUp.setRecoveryProgress(recoveryProgress);
        followUp.setNotes(notes);
        followUp.setStatus("COMPLETED");
        followUp.setCompletedAt(LocalDateTime.now());
        return convertToDTO(patientFollowUpRepository.save(followUp));
    }
    
    private PatientFollowUpDTO convertToDTO(PatientFollowUp followUp) {
        PatientFollowUpDTO dto = new PatientFollowUpDTO();
        dto.setId(followUp.getId());
        dto.setUserId(followUp.getUserId());
        dto.setDoctorId(followUp.getDoctorId());
        dto.setReservationId(followUp.getReservationId());
        dto.setFollowUpDate(followUp.getFollowUpDate());
        dto.setStatus(followUp.getStatus());
        dto.setNotes(followUp.getNotes());
        dto.setRecoveryProgress(followUp.getRecoveryProgress());
        dto.setCreatedAt(followUp.getCreatedAt());
        dto.setCompletedAt(followUp.getCompletedAt());
        return dto;
    }
}

