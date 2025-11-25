package com.healthtourism.hospitalservice.service;

import com.healthtourism.hospitalservice.dto.HospitalDTO;
import com.healthtourism.hospitalservice.entity.Hospital;
import com.healthtourism.hospitalservice.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

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
        return hospitalRepository.findByCity(city)
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
    
    private HospitalDTO convertToDTO(Hospital hospital) {
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
