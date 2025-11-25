package com.healthtourism.translationservice.service;

import com.healthtourism.translationservice.dto.TranslationServiceDTO;
import com.healthtourism.translationservice.entity.TranslationServiceEntity;
import com.healthtourism.translationservice.repository.TranslationServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TranslationServiceService {
    @Autowired
    private TranslationServiceRepository translationServiceRepository;
    
    public List<TranslationServiceDTO> getAllAvailableServices() {
        return translationServiceRepository.findByIsAvailableTrue()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TranslationServiceDTO> getServicesByType(String serviceType) {
        return translationServiceRepository.findByServiceTypeAndIsAvailableTrue(serviceType)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TranslationServiceDTO> getCertifiedTranslators() {
        return translationServiceRepository.findByIsCertifiedTrueAndIsAvailableTrue()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TranslationServiceDTO> getHospitalTranslators() {
        return translationServiceRepository.findByIsAvailableForHospitalTrueAndIsAvailableTrue()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TranslationServiceDTO> getConsultationTranslators() {
        return translationServiceRepository.findByIsAvailableForConsultationTrueAndIsAvailableTrue()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TranslationServiceDTO> getTranslatorsByLanguage(String language) {
        return translationServiceRepository.findByLanguageContaining(language)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TranslationServiceDTO> getTopRatedTranslators() {
        return translationServiceRepository.findAllActiveOrderByRatingDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public TranslationServiceDTO getServiceById(Long id) {
        TranslationServiceEntity service = translationServiceRepository.findByIdAndIsAvailableTrue(id)
                .orElseThrow(() -> new RuntimeException("Çeviri hizmeti bulunamadı"));
        return convertToDTO(service);
    }
    
    public TranslationServiceDTO createService(TranslationServiceEntity service) {
        service.setIsAvailable(true);
        if (service.getRating() == null) service.setRating(0.0);
        if (service.getTotalReviews() == null) service.setTotalReviews(0);
        return convertToDTO(translationServiceRepository.save(service));
    }
    
    private TranslationServiceDTO convertToDTO(TranslationServiceEntity service) {
        TranslationServiceDTO dto = new TranslationServiceDTO();
        dto.setId(service.getId());
        dto.setTranslatorName(service.getTranslatorName());
        dto.setLanguages(service.getLanguages());
        dto.setServiceType(service.getServiceType());
        dto.setIsCertified(service.getIsCertified());
        dto.setIsAvailableForHospital(service.getIsAvailableForHospital());
        dto.setIsAvailableForConsultation(service.getIsAvailableForConsultation());
        dto.setPricePerHour(service.getPricePerHour());
        dto.setPricePerDocument(service.getPricePerDocument());
        dto.setPhone(service.getPhone());
        dto.setEmail(service.getEmail());
        dto.setDescription(service.getDescription());
        dto.setRating(service.getRating());
        dto.setTotalReviews(service.getTotalReviews());
        dto.setIsAvailable(service.getIsAvailable());
        return dto;
    }
}

