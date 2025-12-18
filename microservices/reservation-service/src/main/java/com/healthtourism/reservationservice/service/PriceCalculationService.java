package com.healthtourism.reservationservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

/**
 * Service for calculating reservation prices
 * Calculates total price including:
 * - Doctor consultation fee
 * - Accommodation cost (price per night * number of nights)
 * - Transfer service fee (optional)
 * 
 * Uses Circuit Breaker pattern to handle service failures gracefully
 */
@Service
public class PriceCalculationService {
    
    private RestTemplate restTemplate;
    
    @Value("${doctor.service.url:http://localhost:8003}")
    private String doctorServiceUrl;
    
    @Value("${accommodation.service.url:http://localhost:8004}")
    private String accommodationServiceUrl;
    
    @Value("${transfer.service.url:http://localhost:8007}")
    private String transferServiceUrl;
    
    @Value("${reservation.transfer.fee.default:0}")
    private BigDecimal defaultTransferFee;
    
    @Value("${reservation.doctor.fee.default:500.0}")
    private BigDecimal defaultDoctorFee;
    
    @Value("${reservation.accommodation.price.default:100.0}")
    private BigDecimal defaultAccommodationPricePerNight;
    
    @Autowired(required = false)
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
    
    /**
     * Calculates total price for a reservation
     * 
     * @param doctorId Doctor ID for consultation fee
     * @param accommodationId Accommodation ID (can be null)
     * @param numberOfNights Number of nights for accommodation
     * @param transferId Transfer service ID (can be null)
     * @return Total calculated price
     */
    public BigDecimal calculateTotalPrice(
            Long doctorId,
            Long accommodationId,
            int numberOfNights,
            Long transferId) {
        
        BigDecimal totalPrice = BigDecimal.ZERO;
        
        // 1. Add doctor consultation fee (with Circuit Breaker)
        BigDecimal consultationFee = getDoctorConsultationFee(doctorId);
        totalPrice = totalPrice.add(consultationFee);
        
        // 2. Add accommodation cost (if provided, with Circuit Breaker)
        if (accommodationId != null && numberOfNights > 0) {
            BigDecimal accommodationCost = getAccommodationPrice(accommodationId, numberOfNights);
            totalPrice = totalPrice.add(accommodationCost);
        }
        
        // 3. Add transfer service fee (if provided, with Circuit Breaker)
        if (transferId != null) {
            BigDecimal transferFee = getTransferServicePrice(transferId);
            totalPrice = totalPrice.add(transferFee);
        } else {
            // Add default transfer fee if no specific transfer service selected
            totalPrice = totalPrice.add(defaultTransferFee);
        }
        
        return totalPrice;
    }
    
    /**
     * Gets doctor consultation fee from doctor service
     * Uses Circuit Breaker to handle service failures gracefully
     */
    @CircuitBreaker(name = "doctorService", fallbackMethod = "getDoctorConsultationFeeFallback")
    @Retry(name = "doctorService")
    private BigDecimal getDoctorConsultationFee(Long doctorId) {
        String url = doctorServiceUrl + "/api/doctors/" + doctorId;
        DoctorResponse response = getRestTemplate().getForObject(url, DoctorResponse.class);
        if (response != null && response.getConsultationFee() != null) {
            return BigDecimal.valueOf(response.getConsultationFee());
        }
        return defaultDoctorFee;
    }
    
    /**
     * Fallback method when doctor service is unavailable
     */
    private BigDecimal getDoctorConsultationFeeFallback(Long doctorId, Exception e) {
        System.err.println("Doctor service unavailable, using default fee. Error: " + e.getMessage());
        return defaultDoctorFee;
    }
    
    /**
     * Gets accommodation price per night and calculates total cost
     * Uses Circuit Breaker to handle service failures gracefully
     */
    @CircuitBreaker(name = "accommodationService", fallbackMethod = "getAccommodationPriceFallback")
    @Retry(name = "accommodationService")
    private BigDecimal getAccommodationPrice(Long accommodationId, int numberOfNights) {
        String url = accommodationServiceUrl + "/api/accommodations/" + accommodationId;
        AccommodationResponse response = getRestTemplate().getForObject(url, AccommodationResponse.class);
        if (response != null && response.getPricePerNight() != null) {
            return response.getPricePerNight().multiply(BigDecimal.valueOf(numberOfNights));
        }
        return defaultAccommodationPricePerNight.multiply(BigDecimal.valueOf(numberOfNights));
    }
    
    /**
     * Fallback method when accommodation service is unavailable
     */
    private BigDecimal getAccommodationPriceFallback(Long accommodationId, int numberOfNights, Exception e) {
        System.err.println("Accommodation service unavailable, using default price. Error: " + e.getMessage());
        return defaultAccommodationPricePerNight.multiply(BigDecimal.valueOf(numberOfNights));
    }
    
    /**
     * Gets transfer service price
     * Uses Circuit Breaker to handle service failures gracefully
     */
    @CircuitBreaker(name = "transferService", fallbackMethod = "getTransferServicePriceFallback")
    @Retry(name = "transferService")
    private BigDecimal getTransferServicePrice(Long transferId) {
        String url = transferServiceUrl + "/api/transfers/" + transferId;
        TransferResponse response = getRestTemplate().getForObject(url, TransferResponse.class);
        if (response != null && response.getPrice() != null) {
            return response.getPrice();
        }
        return defaultTransferFee;
    }
    
    /**
     * Fallback method when transfer service is unavailable
     */
    private BigDecimal getTransferServicePriceFallback(Long transferId, Exception e) {
        System.err.println("Transfer service unavailable, using default fee. Error: " + e.getMessage());
        return defaultTransferFee;
    }
    
    // Inner classes for API responses
    private static class DoctorResponse {
        private Double consultationFee;
        public Double getConsultationFee() { return consultationFee; }
        public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }
    }
    
    private static class AccommodationResponse {
        private BigDecimal pricePerNight;
        public BigDecimal getPricePerNight() { return pricePerNight; }
        public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
    }
    
    private static class TransferResponse {
        private BigDecimal price;
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }
}
