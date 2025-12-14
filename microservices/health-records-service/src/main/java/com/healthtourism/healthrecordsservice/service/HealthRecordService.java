package com.healthtourism.healthrecordsservice.service;

import com.healthtourism.healthrecordsservice.dto.HealthRecordDTO;
import com.healthtourism.healthrecordsservice.entity.HealthRecord;
import com.healthtourism.healthrecordsservice.repository.HealthRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthRecordService {

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    public List<HealthRecordDTO> getAll(Long userId) {
        return healthRecordRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public HealthRecordDTO getById(Long id) {
        HealthRecord record = healthRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sağlık kaydı bulunamadı"));
        return convertToDTO(record);
    }

    public HealthRecordDTO create(HealthRecordDTO dto) {
        HealthRecord record = convertToEntity(dto);
        HealthRecord saved = healthRecordRepository.save(record);
        return convertToDTO(saved);
    }

    public HealthRecordDTO update(Long id, HealthRecordDTO dto) {
        HealthRecord record = healthRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sağlık kaydı bulunamadı"));
        
        record.setType(dto.getType());
        record.setDate(dto.getDate());
        record.setDoctorName(dto.getDoctorName());
        record.setHospitalName(dto.getHospitalName());
        record.setDescription(dto.getDescription());
        record.setDocumentCount(dto.getDocumentCount());
        
        HealthRecord updated = healthRecordRepository.save(record);
        return convertToDTO(updated);
    }

    public void delete(Long id) {
        healthRecordRepository.deleteById(id);
    }

    private HealthRecordDTO convertToDTO(HealthRecord record) {
        HealthRecordDTO dto = new HealthRecordDTO();
        dto.setId(record.getId());
        dto.setUserId(record.getUserId());
        dto.setType(record.getType());
        dto.setDate(record.getDate());
        dto.setDoctorName(record.getDoctorName());
        dto.setHospitalName(record.getHospitalName());
        dto.setDescription(record.getDescription());
        dto.setDocumentCount(record.getDocumentCount());
        return dto;
    }

    private HealthRecord convertToEntity(HealthRecordDTO dto) {
        HealthRecord record = new HealthRecord();
        record.setUserId(dto.getUserId());
        record.setType(dto.getType());
        record.setDate(dto.getDate());
        record.setDoctorName(dto.getDoctorName());
        record.setHospitalName(dto.getHospitalName());
        record.setDescription(dto.getDescription());
        record.setDocumentCount(dto.getDocumentCount() != null ? dto.getDocumentCount() : 0);
        return record;
    }
}

