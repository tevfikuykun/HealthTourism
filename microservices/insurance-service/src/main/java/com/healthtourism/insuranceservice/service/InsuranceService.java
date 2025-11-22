package com.healthtourism.insuranceservice.service;
import com.healthtourism.insuranceservice.dto.InsuranceDTO;
import com.healthtourism.insuranceservice.entity.Insurance;
import com.healthtourism.insuranceservice.repository.InsuranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InsuranceService {
    @Autowired
    private InsuranceRepository insuranceRepository;
    
    public List<InsuranceDTO> getAllActiveInsurances() {
        return insuranceRepository.findAllActiveOrderByRatingDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<InsuranceDTO> getInsurancesByType(String insuranceType) {
        return insuranceRepository.findByInsuranceTypeAndIsActiveTrue(insuranceType)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<InsuranceDTO> getInsurancesByCoverageArea(String coverageArea) {
        return insuranceRepository.findByCoverageAreaAndIsActiveTrue(coverageArea)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<InsuranceDTO> getInsurancesByPremium(BigDecimal maxPremium) {
        return insuranceRepository.findByPremiumLessThanEqual(maxPremium)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public InsuranceDTO getInsuranceById(Long id) {
        Insurance insurance = insuranceRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Sigorta bulunamadı"));
        return convertToDTO(insurance);
    }
    
    private InsuranceDTO convertToDTO(Insurance insurance) {
        InsuranceDTO dto = new InsuranceDTO();
        dto.setId(insurance.getId());
        dto.setCompanyName(insurance.getCompanyName());
        dto.setInsuranceType(insurance.getInsuranceType());
        dto.setCoverageArea(insurance.getCoverageArea());
        dto.setCoverageAmount(insurance.getCoverageAmount());
        dto.setPremium(insurance.getPremium());
        dto.setValidityDays(insurance.getValidityDays());
        dto.setCoverageDetails(insurance.getCoverageDetails());
        dto.setExclusions(insurance.getExclusions());
        dto.setPhone(insurance.getPhone());
        dto.setEmail(insurance.getEmail());
        dto.setDescription(insurance.getDescription());
        dto.setRating(insurance.getRating());
        dto.setTotalReviews(insurance.getTotalReviews());
        dto.setIsActive(insurance.getIsActive());
        return dto;
    }
}

