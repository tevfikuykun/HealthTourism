package com.healthtourism.promotionservice.service;

import com.healthtourism.promotionservice.dto.PromotionDTO;
import com.healthtourism.promotionservice.dto.PromotionValidationRequest;
import com.healthtourism.promotionservice.dto.PromotionValidationResponse;
import com.healthtourism.promotionservice.entity.Promotion;
import com.healthtourism.promotionservice.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService {
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    public List<PromotionDTO> getAllActivePromotions() {
        return promotionRepository.findActivePromotions(LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<PromotionDTO> getPromotionsByServiceType(String serviceType) {
        return promotionRepository.findByApplicableServiceType(serviceType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public PromotionDTO getPromotionByCode(String code) {
        Promotion promotion = promotionRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Promosyon kodu bulunamadı"));
        return convertToDTO(promotion);
    }
    
    @Transactional
    public PromotionValidationResponse validatePromotion(PromotionValidationRequest request) {
        PromotionValidationResponse response = new PromotionValidationResponse();
        
        Promotion promotion = promotionRepository
                .findValidPromotionByCode(request.getCode(), LocalDateTime.now())
                .orElse(null);
        
        if (promotion == null) {
            response.setIsValid(false);
            response.setMessage("Geçersiz veya süresi dolmuş promosyon kodu");
            return response;
        }
        
        // Minimum alışveriş tutarı kontrolü
        if (request.getPurchaseAmount().compareTo(promotion.getMinPurchaseAmount()) < 0) {
            response.setIsValid(false);
            response.setMessage("Minimum alışveriş tutarı: " + promotion.getMinPurchaseAmount() + " TRY");
            return response;
        }
        
        // Servis tipi kontrolü
        if (!promotion.getApplicableServiceType().equals("ALL") && 
            !promotion.getApplicableServiceType().equals(request.getServiceType())) {
            response.setIsValid(false);
            response.setMessage("Bu promosyon kodu bu hizmet için geçerli değil");
            return response;
        }
        
        // Belirli servis kontrolü
        if (promotion.getSpecificServiceId() != null && 
            !promotion.getSpecificServiceId().equals(request.getServiceId())) {
            response.setIsValid(false);
            response.setMessage("Bu promosyon kodu bu servis için geçerli değil");
            return response;
        }
        
        // İndirim hesaplama
        BigDecimal discountAmount;
        if (promotion.getDiscountType().equals("PERCENTAGE")) {
            discountAmount = request.getPurchaseAmount()
                    .multiply(promotion.getDiscountValue())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            
            // Maksimum indirim tutarı kontrolü
            if (discountAmount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
                discountAmount = promotion.getMaxDiscountAmount();
            }
        } else {
            discountAmount = promotion.getDiscountValue();
        }
        
        BigDecimal finalAmount = request.getPurchaseAmount().subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }
        
        response.setIsValid(true);
        response.setMessage("Promosyon kodu geçerli");
        response.setDiscountAmount(discountAmount);
        response.setFinalAmount(finalAmount);
        response.setPromotion(convertToDTO(promotion));
        
        return response;
    }
    
    @Transactional
    public PromotionDTO applyPromotion(String code) {
        Promotion promotion = promotionRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Promosyon kodu bulunamadı"));
        
        if (promotion.getUsedCount() >= promotion.getMaxUses()) {
            throw new RuntimeException("Promosyon kodu kullanım limitine ulaştı");
        }
        
        promotion.setUsedCount(promotion.getUsedCount() + 1);
        promotion = promotionRepository.save(promotion);
        
        return convertToDTO(promotion);
    }
    
    public PromotionDTO createPromotion(Promotion promotion) {
        promotion.setUsedCount(0);
        promotion.setIsActive(true);
        return convertToDTO(promotionRepository.save(promotion));
    }
    
    private PromotionDTO convertToDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setId(promotion.getId());
        dto.setCode(promotion.getCode());
        dto.setName(promotion.getName());
        dto.setDescription(promotion.getDescription());
        dto.setDiscountType(promotion.getDiscountType());
        dto.setDiscountValue(promotion.getDiscountValue());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setMaxUses(promotion.getMaxUses());
        dto.setUsedCount(promotion.getUsedCount());
        dto.setMinPurchaseAmount(promotion.getMinPurchaseAmount());
        dto.setMaxDiscountAmount(promotion.getMaxDiscountAmount());
        dto.setIsActive(promotion.getIsActive());
        dto.setApplicableServiceType(promotion.getApplicableServiceType());
        dto.setSpecificServiceId(promotion.getSpecificServiceId());
        dto.setCreatedAt(promotion.getCreatedAt());
        return dto;
    }
}

