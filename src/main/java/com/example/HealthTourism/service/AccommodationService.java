package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.AccommodationDTO;
import com.example.HealthTourism.entity.Accommodation;
import com.example.HealthTourism.repository.AccommodationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccommodationService {
    
    @Autowired
    private AccommodationRepository accommodationRepository;
    
    public List<AccommodationDTO> getAccommodationsByHospital(Long hospitalId) {
        return accommodationRepository.findByHospitalIdAndIsActiveTrue(hospitalId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AccommodationDTO> getAccommodationsNearHospital(Long hospitalId) {
        return accommodationRepository.findByHospitalIdOrderByDistanceAsc(hospitalId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AccommodationDTO> getAccommodationsByPrice(BigDecimal maxPrice) {
        return accommodationRepository.findByPricePerNightLessThanEqual(maxPrice)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AccommodationDTO> getTopRatedAccommodationsByHospital(Long hospitalId) {
        return accommodationRepository.findByHospitalIdOrderByRatingDesc(hospitalId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public AccommodationDTO getAccommodationById(Long id) {
        Accommodation accommodation = accommodationRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Konaklama bulunamadı"));
        return convertToDTO(accommodation);
    }
    
    public AccommodationDTO createAccommodation(Accommodation accommodation) {
        accommodation.setIsActive(true);
        accommodation.setRating(0.0);
        accommodation.setTotalReviews(0);
        Accommodation saved = accommodationRepository.save(accommodation);
        return convertToDTO(saved);
    }
    
    private AccommodationDTO convertToDTO(Accommodation accommodation) {
        AccommodationDTO dto = new AccommodationDTO();
        dto.setId(accommodation.getId());
        dto.setName(accommodation.getName());
        dto.setType(accommodation.getType());
        dto.setAddress(accommodation.getAddress());
        dto.setCity(accommodation.getCity());
        dto.setDistrict(accommodation.getDistrict());
        dto.setPhone(accommodation.getPhone());
        dto.setEmail(accommodation.getEmail());
        dto.setDescription(accommodation.getDescription());
        dto.setLatitude(accommodation.getLatitude());
        dto.setLongitude(accommodation.getLongitude());
        dto.setDistanceToHospital(accommodation.getDistanceToHospital());
        dto.setDistanceToHospitalMinutes(accommodation.getDistanceToHospitalMinutes());
        dto.setAirportDistance(accommodation.getAirportDistance());
        dto.setAirportDistanceMinutes(accommodation.getAirportDistanceMinutes());
        dto.setPricePerNight(accommodation.getPricePerNight());
        dto.setStarRating(accommodation.getStarRating());
        dto.setRating(accommodation.getRating());
        dto.setTotalReviews(accommodation.getTotalReviews());
        dto.setHasWifi(accommodation.getHasWifi());
        dto.setHasParking(accommodation.getHasParking());
        dto.setHasBreakfast(accommodation.getHasBreakfast());
        dto.setHospitalId(accommodation.getHospital().getId());
        dto.setHospitalName(accommodation.getHospital().getName());
        return dto;
    }
}

