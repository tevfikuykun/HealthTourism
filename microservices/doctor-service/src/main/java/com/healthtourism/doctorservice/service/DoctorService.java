package com.healthtourism.doctorservice.service;

import com.healthtourism.doctorservice.dto.DoctorDTO;
import com.healthtourism.doctorservice.entity.Doctor;
import com.healthtourism.doctorservice.exception.ResourceNotFoundException;
import com.healthtourism.doctorservice.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;
    
    public List<DoctorDTO> getDoctorsByHospital(Long hospitalId) {
        return doctorRepository.findByHospitalIdAndIsAvailableTrue(hospitalId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationOrderByRatingDesc(specialization)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findByIdAndIsAvailableTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doktor bulunamadı: " + id));
        return convertToDTO(doctor);
    }
    
    public List<DoctorDTO> getTopRatedDoctorsByHospital(Long hospitalId) {
        return doctorRepository.findByHospitalIdOrderByRatingDesc(hospitalId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public DoctorDTO createDoctor(Doctor doctor) {
        doctor.setIsAvailable(true);
        doctor.setRating(0.0);
        doctor.setTotalReviews(0);
        return convertToDTO(doctorRepository.save(doctor));
    }
    
    public DoctorDTO uploadDoctorImage(Long id, org.springframework.web.multipart.MultipartFile file) {
        Doctor doctor = doctorRepository.findByIdAndIsAvailableTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Doktor bulunamadı: " + id));
        
        // Call file-storage-service to upload image
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
            
            org.springframework.util.LinkedMultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
            byte[] fileBytes = file.getBytes();
            org.springframework.core.io.ByteArrayResource resource = new org.springframework.core.io.ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", resource);
            
            org.springframework.http.HttpEntity<org.springframework.util.LinkedMultiValueMap<String, Object>> requestEntity = 
                new org.springframework.http.HttpEntity<>(body, headers);
            
            org.springframework.http.ResponseEntity<java.util.Map> response = 
                restTemplate.postForEntity(
                    "http://localhost:8027/api/files/upload/image/doctor/" + id,
                    requestEntity,
                    java.util.Map.class
                );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                java.util.Map<String, Object> fileMetadata = response.getBody();
                Long fileId = Long.valueOf(fileMetadata.get("id").toString());
                doctor.setImageUrl("http://localhost:8027/api/files/" + fileId);
                doctor.setThumbnailUrl("http://localhost:8027/api/files/" + fileId + "/thumbnail");
                doctor = doctorRepository.save(doctor);
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
        
        return convertToDTO(doctor);
    }
    
    private DoctorDTO convertToDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setFullName(doctor.getTitle() + " " + doctor.getFirstName() + " " + doctor.getLastName());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setTitle(doctor.getTitle());
        dto.setBio(doctor.getBio());
        dto.setExperienceYears(doctor.getExperienceYears());
        dto.setLanguages(doctor.getLanguages());
        dto.setRating(doctor.getRating());
        dto.setTotalReviews(doctor.getTotalReviews());
        dto.setConsultationFee(doctor.getConsultationFee());
        dto.setIsAvailable(doctor.getIsAvailable());
        dto.setHospitalId(doctor.getHospitalId());
        dto.setImageUrl(doctor.getImageUrl());
        dto.setThumbnailUrl(doctor.getThumbnailUrl());
        return dto;
    }
}

