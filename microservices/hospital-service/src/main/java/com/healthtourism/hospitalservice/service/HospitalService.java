package com.healthtourism.hospitalservice.service;

import com.healthtourism.hospitalservice.dto.HospitalDTO;
import com.healthtourism.hospitalservice.entity.Hospital;
import com.healthtourism.hospitalservice.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HospitalService {
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Cacheable(value = "hospitals", key = "#id")
    public HospitalDTO getHospitalById(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Hastane bulunamadı"));
        return convertToDTO(hospital);
    }
    
    @Cacheable(value = "hospitals", key = "'all-active'")
    public List<HospitalDTO> getAllActiveHospitals() {
        return hospitalRepository.findAllActiveOrderByRatingDesc()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Cacheable(value = "hospitals", key = "#city")
    public List<HospitalDTO> getHospitalsByCity(String city) {
        return hospitalRepository.findByCityAndIsActiveTrue(city)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @CacheEvict(value = "hospitals", allEntries = true)
    public HospitalDTO createHospital(Hospital hospital) {
        Hospital saved = hospitalRepository.save(hospital);
        return convertToDTO(saved);
    }
    
    @CacheEvict(value = "hospitals", key = "#id")
    public HospitalDTO updateHospital(Long id, Hospital hospital) {
        Hospital existing = hospitalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Hastane bulunamadı"));
        
        existing.setName(hospital.getName());
        existing.setAddress(hospital.getAddress());
        existing.setCity(hospital.getCity());
        existing.setAirportDistance(hospital.getAirportDistance());
        existing.setRating(hospital.getRating());
        existing.setIsActive(hospital.getIsActive());
        
        Hospital saved = hospitalRepository.save(existing);
        return convertToDTO(saved);
    }
    
    public List<HospitalDTO> getHospitalsByDistrict(String district) {
        return hospitalRepository.findByDistrictAndIsActiveTrue(district)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<HospitalDTO> getHospitalsNearAirport(Double maxDistance) {
        return hospitalRepository.findByAirportDistanceLessThanEqual(maxDistance)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @CacheEvict(value = "hospitals", key = "#id")
    public HospitalDTO uploadHospitalImage(Long id, org.springframework.web.multipart.MultipartFile file) {
        Hospital hospital = hospitalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Hastane bulunamadı: " + id));
        
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
                    "http://localhost:8027/api/files/upload/image/hospital/" + id,
                    requestEntity,
                    java.util.Map.class
                );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                java.util.Map<String, Object> fileMetadata = response.getBody();
                Long fileId = Long.valueOf(fileMetadata.get("id").toString());
                hospital.setImageUrl("http://localhost:8027/api/files/" + fileId);
                hospital.setThumbnailUrl("http://localhost:8027/api/files/" + fileId + "/thumbnail");
                hospital = hospitalRepository.save(hospital);
            }
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
        
        return convertToDTO(hospital);
    }
    
    public HospitalDTO convertToDTO(Hospital hospital) {
        HospitalDTO dto = new HospitalDTO();
        dto.setId(hospital.getId());
        dto.setName(hospital.getName());
        dto.setAddress(hospital.getAddress());
        dto.setCity(hospital.getCity());
        dto.setDistrict(hospital.getDistrict());
        dto.setPhone(hospital.getPhone());
        dto.setEmail(hospital.getEmail());
        dto.setDescription(hospital.getDescription());
        dto.setLatitude(hospital.getLatitude());
        dto.setLongitude(hospital.getLongitude());
        dto.setAirportDistance(hospital.getAirportDistance());
        dto.setAirportDistanceMinutes(hospital.getAirportDistanceMinutes());
        dto.setRating(hospital.getRating());
        dto.setTotalReviews(hospital.getTotalReviews());
        return dto;
    }
}
