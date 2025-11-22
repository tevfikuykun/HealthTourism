package com.healthtourism.packageservice.service;
import com.healthtourism.packageservice.dto.TravelPackageDTO;
import com.healthtourism.packageservice.entity.TravelPackage;
import com.healthtourism.packageservice.repository.TravelPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelPackageService {
    @Autowired
    private TravelPackageRepository travelPackageRepository;
    
    public List<TravelPackageDTO> getAllActivePackages() {
        return travelPackageRepository.findByIsActiveTrue()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TravelPackageDTO> getPackagesByHospital(Long hospitalId) {
        return travelPackageRepository.findByHospitalIdAndIsActiveTrue(hospitalId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TravelPackageDTO> getPackagesByType(String packageType) {
        return travelPackageRepository.findByPackageTypeAndIsActiveTrue(packageType)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public TravelPackageDTO getPackageById(Long id) {
        TravelPackage travelPackage = travelPackageRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Paket bulunamadı"));
        return convertToDTO(travelPackage);
    }
    
    private TravelPackageDTO convertToDTO(TravelPackage travelPackage) {
        TravelPackageDTO dto = new TravelPackageDTO();
        dto.setId(travelPackage.getId());
        dto.setPackageName(travelPackage.getPackageName());
        dto.setPackageType(travelPackage.getPackageType());
        dto.setDurationDays(travelPackage.getDurationDays());
        dto.setTotalPrice(travelPackage.getTotalPrice());
        dto.setDiscountPercentage(travelPackage.getDiscountPercentage());
        dto.setFinalPrice(travelPackage.getFinalPrice());
        dto.setIncludesFlight(travelPackage.getIncludesFlight());
        dto.setIncludesAccommodation(travelPackage.getIncludesAccommodation());
        dto.setIncludesTransfer(travelPackage.getIncludesTransfer());
        dto.setIncludesCarRental(travelPackage.getIncludesCarRental());
        dto.setIncludesVisaService(travelPackage.getIncludesVisaService());
        dto.setIncludesTranslation(travelPackage.getIncludesTranslation());
        dto.setIncludesInsurance(travelPackage.getIncludesInsurance());
        dto.setDescription(travelPackage.getDescription());
        dto.setRating(travelPackage.getRating());
        dto.setTotalReviews(travelPackage.getTotalReviews());
        dto.setIsActive(travelPackage.getIsActive());
        dto.setHospitalId(travelPackage.getHospitalId());
        dto.setDoctorId(travelPackage.getDoctorId());
        dto.setAccommodationId(travelPackage.getAccommodationId());
        return dto;
    }
}

