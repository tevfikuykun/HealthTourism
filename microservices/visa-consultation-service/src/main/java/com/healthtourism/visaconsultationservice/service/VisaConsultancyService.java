package com.healthtourism.visaconsultationservice.service;

import com.healthtourism.visaconsultationservice.dto.VisaConsultancyDTO;
import com.healthtourism.visaconsultationservice.entity.VisaConsultancy;
import com.healthtourism.visaconsultationservice.repository.VisaConsultancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisaConsultancyService {
    @Autowired
    private VisaConsultancyRepository visaConsultancyRepository;
    
    public List<VisaConsultancyDTO> getAllAvailableConsultancies() {
        return visaConsultancyRepository.findByIsAvailableTrue()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<VisaConsultancyDTO> getConsultanciesByCountry(String country) {
        return visaConsultancyRepository.findByCountryAndIsAvailableTrue(country)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<VisaConsultancyDTO> getConsultanciesByVisaType(String visaType) {
        return visaConsultancyRepository.findByVisaTypeAndIsAvailableTrue(visaType)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<VisaConsultancyDTO> getConsultanciesByCountryAndVisaType(String country, String visaType) {
        return visaConsultancyRepository.findByCountryAndVisaTypeAndIsAvailableTrue(country, visaType)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<VisaConsultancyDTO> getTopRatedConsultancies() {
        return visaConsultancyRepository.findAllActiveOrderByRatingDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public VisaConsultancyDTO getConsultancyById(Long id) {
        VisaConsultancy consultancy = visaConsultancyRepository.findByIdAndIsAvailableTrue(id)
                .orElseThrow(() -> new RuntimeException("Vize danışmanlık hizmeti bulunamadı"));
        return convertToDTO(consultancy);
    }
    
    public VisaConsultancyDTO createConsultancy(VisaConsultancy consultancy) {
        consultancy.setIsAvailable(true);
        if (consultancy.getRating() == null) consultancy.setRating(0.0);
        if (consultancy.getTotalReviews() == null) consultancy.setTotalReviews(0);
        return convertToDTO(visaConsultancyRepository.save(consultancy));
    }
    
    private VisaConsultancyDTO convertToDTO(VisaConsultancy consultancy) {
        VisaConsultancyDTO dto = new VisaConsultancyDTO();
        dto.setId(consultancy.getId());
        dto.setCompanyName(consultancy.getCompanyName());
        dto.setVisaType(consultancy.getVisaType());
        dto.setCountry(consultancy.getCountry());
        dto.setServiceFee(consultancy.getServiceFee());
        dto.setProcessingDays(consultancy.getProcessingDays());
        dto.setIncludesTranslation(consultancy.getIncludesTranslation());
        dto.setIncludesDocumentPreparation(consultancy.getIncludesDocumentPreparation());
        dto.setIncludesAppointmentBooking(consultancy.getIncludesAppointmentBooking());
        dto.setPhone(consultancy.getPhone());
        dto.setEmail(consultancy.getEmail());
        dto.setDescription(consultancy.getDescription());
        dto.setRating(consultancy.getRating());
        dto.setTotalReviews(consultancy.getTotalReviews());
        dto.setIsAvailable(consultancy.getIsAvailable());
        return dto;
    }
}

