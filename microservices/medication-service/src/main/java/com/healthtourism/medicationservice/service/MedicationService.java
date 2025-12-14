package com.healthtourism.medicationservice.service;

import com.healthtourism.medicationservice.dto.MedicationDTO;
import com.healthtourism.medicationservice.entity.Medication;
import com.healthtourism.medicationservice.repository.MedicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicationService {
    @Autowired
    private MedicationRepository medicationRepository;

    public List<MedicationDTO> getAll(Long userId) {
        return medicationRepository.findByUserId(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public MedicationDTO getById(Long id) {
        Medication med = medicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İlaç bulunamadı"));
        return convertToDTO(med);
    }

    public MedicationDTO create(MedicationDTO dto) {
        Medication med = convertToEntity(dto);
        return convertToDTO(medicationRepository.save(med));
    }

    public MedicationDTO update(Long id, MedicationDTO dto) {
        Medication med = medicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İlaç bulunamadı"));
        med.setName(dto.getName());
        med.setDosage(dto.getDosage());
        med.setTime(dto.getTime());
        med.setFrequency(dto.getFrequency());
        med.setIsActive(dto.getIsActive());
        return convertToDTO(medicationRepository.save(med));
    }

    public void delete(Long id) {
        medicationRepository.deleteById(id);
    }

    public MedicationDTO toggle(Long id) {
        Medication med = medicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İlaç bulunamadı"));
        med.setIsActive(!med.getIsActive());
        return convertToDTO(medicationRepository.save(med));
    }

    private MedicationDTO convertToDTO(Medication med) {
        MedicationDTO dto = new MedicationDTO();
        dto.setId(med.getId());
        dto.setUserId(med.getUserId());
        dto.setName(med.getName());
        dto.setDosage(med.getDosage());
        dto.setTime(med.getTime());
        dto.setFrequency(med.getFrequency());
        dto.setIsActive(med.getIsActive());
        return dto;
    }

    private Medication convertToEntity(MedicationDTO dto) {
        Medication med = new Medication();
        med.setUserId(dto.getUserId());
        med.setName(dto.getName());
        med.setDosage(dto.getDosage());
        med.setTime(dto.getTime());
        med.setFrequency(dto.getFrequency());
        med.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return med;
    }
}

