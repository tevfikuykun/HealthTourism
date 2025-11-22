package com.healthtourism.hospitalservice.service;

import com.healthtourism.hospitalservice.dto.HospitalDTO;
import com.healthtourism.hospitalservice.entity.Hospital;
import com.healthtourism.hospitalservice.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;
    
    public List<HospitalDTO> getAllActiveHospitals() {
        return hospitalRepository.findAllActiveOrderByRatingDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public HospitalDTO getHospitalById(Long id) {
        Hospital hospital = hospitalRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Hastane bulunamadı"));
        return convertToDTO(hospital);
    }
    
    public List<HospitalDTO> getHospitalsByCity(String city) {
        return hospitalRepository.findByCityAndIsActiveTrue(city)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<HospitalDTO> getHospitalsByDistrict(String district) {
        return hospitalRepository.findByDistrictAndIsActiveTrue(district)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<HospitalDTO> getHospitalsNearAirport(Double maxDistance) {
        return hospitalRepository.findByAirportDistanceLessThanEqual(maxDistance)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public HospitalDTO createHospital(Hospital hospital) {
        hospital.setIsActive(true);
        hospital.setRating(0.0);
        hospital.setTotalReviews(0);
        return convertToDTO(hospitalRepository.save(hospital));
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

