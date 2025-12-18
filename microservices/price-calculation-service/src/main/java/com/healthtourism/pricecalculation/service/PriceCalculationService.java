package com.healthtourism.pricecalculation.service;

import com.healthtourism.pricecalculation.dto.PriceCalculationRequest;
import com.healthtourism.pricecalculation.dto.PriceCalculationResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PriceCalculationService {
    
    private static final BigDecimal TAX_RATE = new BigDecimal("0.18"); // %18 KDV
    private static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal("10000"); // 10.000 TRY üzeri %5 indirim
    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.05");
    
    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request) {
        PriceCalculationResponse response = new PriceCalculationResponse();
        List<PriceCalculationResponse.PriceBreakdown> breakdown = new ArrayList<>();
        
        BigDecimal basePrice = BigDecimal.ZERO;
        
        // Base prices (Hospital + Doctor + Treatment)
        BigDecimal hospitalPrice = request.getHospitalPrice() != null ? request.getHospitalPrice() : BigDecimal.ZERO;
        BigDecimal doctorPrice = request.getDoctorPrice() != null ? request.getDoctorPrice() : BigDecimal.ZERO;
        BigDecimal treatmentPrice = request.getTreatmentPrice() != null ? request.getTreatmentPrice() : BigDecimal.ZERO;
        
        basePrice = basePrice.add(hospitalPrice).add(doctorPrice).add(treatmentPrice);
        
        if (hospitalPrice.compareTo(BigDecimal.ZERO) > 0) {
            breakdown.add(new PriceCalculationResponse.PriceBreakdown("Hospital", hospitalPrice, 1, hospitalPrice));
        }
        if (doctorPrice.compareTo(BigDecimal.ZERO) > 0) {
            breakdown.add(new PriceCalculationResponse.PriceBreakdown("Doctor", doctorPrice, 1, doctorPrice));
        }
        if (treatmentPrice.compareTo(BigDecimal.ZERO) > 0) {
            breakdown.add(new PriceCalculationResponse.PriceBreakdown("Treatment", treatmentPrice, 1, treatmentPrice));
        }
        
        response.setBasePrice(basePrice);
        
        // Flight price
        BigDecimal flightPrice = request.getFlightPrice() != null ? request.getFlightPrice() : BigDecimal.ZERO;
        response.setFlightPrice(flightPrice);
        if (flightPrice.compareTo(BigDecimal.ZERO) > 0) {
            breakdown.add(new PriceCalculationResponse.PriceBreakdown("Flight", flightPrice, 1, flightPrice));
        }
        
        // Accommodation price
        BigDecimal accommodationPrice = BigDecimal.ZERO;
        if (request.getAccommodationPricePerNight() != null && request.getAccommodationNights() != null) {
            accommodationPrice = request.getAccommodationPricePerNight()
                .multiply(new BigDecimal(request.getAccommodationNights()));
            response.setAccommodationPrice(accommodationPrice);
            breakdown.add(new PriceCalculationResponse.PriceBreakdown(
                "Accommodation", 
                request.getAccommodationPricePerNight(), 
                request.getAccommodationNights(), 
                accommodationPrice
            ));
        }
        
        // Additional services
        BigDecimal additionalServicesPrice = BigDecimal.ZERO;
        if (request.getAdditionalServices() != null) {
            for (PriceCalculationRequest.AdditionalService service : request.getAdditionalServices()) {
                BigDecimal serviceTotal = service.getPrice().multiply(new BigDecimal(service.getQuantity()));
                additionalServicesPrice = additionalServicesPrice.add(serviceTotal);
                breakdown.add(new PriceCalculationResponse.PriceBreakdown(
                    service.getServiceType(),
                    service.getPrice(),
                    service.getQuantity(),
                    serviceTotal
                ));
            }
        }
        response.setAdditionalServicesPrice(additionalServicesPrice);
        
        // VIP Transfer
        BigDecimal transferPrice = BigDecimal.ZERO;
        if (request.getVipTransfer() != null && request.getVipTransfer()) {
            transferPrice = request.getTransferPrice() != null ? request.getTransferPrice() : new BigDecimal("500");
            response.setTransferPrice(transferPrice);
            breakdown.add(new PriceCalculationResponse.PriceBreakdown("VIP Transfer", transferPrice, 1, transferPrice));
        }
        
        // Translation service
        BigDecimal translationPrice = BigDecimal.ZERO;
        if (request.getTranslationService() != null && request.getTranslationService()) {
            translationPrice = request.getTranslationPrice() != null ? request.getTranslationPrice() : new BigDecimal("300");
            response.setTranslationPrice(translationPrice);
            breakdown.add(new PriceCalculationResponse.PriceBreakdown("Translation Service", translationPrice, 1, translationPrice));
        }
        
        // Insurance
        BigDecimal insurancePrice = BigDecimal.ZERO;
        if (request.getInsurance() != null && request.getInsurance()) {
            insurancePrice = request.getInsurancePrice() != null ? request.getInsurancePrice() : new BigDecimal("200");
            response.setInsurancePrice(insurancePrice);
            breakdown.add(new PriceCalculationResponse.PriceBreakdown("Insurance", insurancePrice, 1, insurancePrice));
        }
        
        // Calculate subtotal
        BigDecimal subtotal = basePrice
            .add(flightPrice)
            .add(accommodationPrice)
            .add(additionalServicesPrice)
            .add(transferPrice)
            .add(translationPrice)
            .add(insurancePrice);
        
        response.setSubtotal(subtotal);
        
        // Calculate discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (subtotal.compareTo(DISCOUNT_THRESHOLD) > 0) {
            discountAmount = subtotal.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
        }
        response.setDiscountAmount(discountAmount);
        
        // Calculate tax (on subtotal - discount)
        BigDecimal taxableAmount = subtotal.subtract(discountAmount);
        BigDecimal taxAmount = taxableAmount.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        response.setTaxAmount(taxAmount);
        
        // Calculate total
        BigDecimal totalPrice = taxableAmount.add(taxAmount);
        response.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
        response.setCurrency(request.getCurrency());
        response.setBreakdown(breakdown);
        
        return response;
    }
    
    public PriceCalculationResponse calculatePriceWithDiscount(PriceCalculationRequest request, BigDecimal discountPercentage) {
        PriceCalculationResponse response = calculatePrice(request);
        
        BigDecimal discountAmount = response.getSubtotal()
            .multiply(discountPercentage.divide(new BigDecimal("100")))
            .setScale(2, RoundingMode.HALF_UP);
        
        response.setDiscountAmount(response.getDiscountAmount().add(discountAmount));
        
        BigDecimal taxableAmount = response.getSubtotal().subtract(response.getDiscountAmount());
        BigDecimal taxAmount = taxableAmount.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        response.setTaxAmount(taxAmount);
        
        BigDecimal totalPrice = taxableAmount.add(taxAmount);
        response.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
        
        return response;
    }
}
