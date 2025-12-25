package com.healthtourism.reservationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Price Calculation Service
 * 
 * Centralized price calculation logic for reservations.
 * Separates pricing concerns from business logic.
 * 
 * Business Rules:
 * - Doctor consultation fee (one-time)
 * - Accommodation cost = dailyPrice * numberOfNights
 * - Optional: Seasonal discounts, promotions, etc.
 * - Currency support
 */
@Service
public class PriceCalculationService {
    
    private static final Logger logger = LoggerFactory.getLogger(PriceCalculationService.class);
    
    /**
     * Calculate total price for a reservation
     * 
     * @param doctorFee Doctor consultation fee
     * @param accommodationDailyPrice Accommodation daily price (can be null)
     * @param numberOfNights Number of nights
     * @param currency Currency code (EUR, USD, TRY, etc.)
     * @return Calculated total price
     */
    public BigDecimal calculateTotal(
            BigDecimal doctorFee,
            BigDecimal accommodationDailyPrice,
            Integer numberOfNights,
            String currency) {
        
        logger.debug("Calculating total price: doctorFee={}, accommodationDailyPrice={}, nights={}, currency={}",
            doctorFee, accommodationDailyPrice, numberOfNights, currency);
        
        if (doctorFee == null) {
            throw new IllegalArgumentException("Doctor fee cannot be null");
        }
        
        BigDecimal total = doctorFee;
        
        // Calculate accommodation cost if provided
        if (accommodationDailyPrice != null && numberOfNights != null && numberOfNights > 0) {
            BigDecimal accommodationCost = accommodationDailyPrice
                    .multiply(BigDecimal.valueOf(numberOfNights));
            total = total.add(accommodationCost);
        }
        
        // Round to 2 decimal places (standard for currency)
        total = total.setScale(2, RoundingMode.HALF_UP);
        
        logger.debug("Calculated total price: {}", total);
        
        return total;
    }
    
    /**
     * Calculate accommodation cost separately
     * 
     * @param accommodationDailyPrice Daily price
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @return Accommodation cost
     */
    public BigDecimal calculateAccommodationCost(
            BigDecimal accommodationDailyPrice,
            LocalDate checkInDate,
            LocalDate checkOutDate) {
        
        if (accommodationDailyPrice == null || checkInDate == null || checkOutDate == null) {
            return BigDecimal.ZERO;
        }
        
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        
        return accommodationDailyPrice
                .multiply(BigDecimal.valueOf(nights))
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Apply discount to price
     * 
     * @param basePrice Base price
     * @param discountPercentage Discount percentage (e.g., 10 for 10%)
     * @return Price with discount applied
     */
    public BigDecimal applyDiscount(BigDecimal basePrice, BigDecimal discountPercentage) {
        if (basePrice == null || discountPercentage == null) {
            return basePrice;
        }
        
        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 || 
            discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        
        BigDecimal discountAmount = basePrice
                .multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        
        BigDecimal finalPrice = basePrice.subtract(discountAmount);
        
        logger.debug("Applied {}% discount: {} -> {}", discountPercentage, basePrice, finalPrice);
        
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Apply seasonal pricing multiplier
     * 
     * @param basePrice Base price
     * @param multiplier Seasonal multiplier (e.g., 1.2 for 20% increase)
     * @return Price with seasonal adjustment
     */
    public BigDecimal applySeasonalMultiplier(BigDecimal basePrice, BigDecimal multiplier) {
        if (basePrice == null || multiplier == null) {
            return basePrice;
        }
        
        if (multiplier.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Multiplier must be positive");
        }
        
        BigDecimal adjustedPrice = basePrice.multiply(multiplier);
        
        logger.debug("Applied seasonal multiplier {}: {} -> {}", multiplier, basePrice, adjustedPrice);
        
        return adjustedPrice.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Convert price to different currency
     * 
     * @param amount Amount to convert
     * @param fromCurrency Source currency
     * @param toCurrency Target currency
     * @param exchangeRate Exchange rate (toCurrency / fromCurrency)
     * @return Converted amount
     */
    public BigDecimal convertCurrency(
            BigDecimal amount,
            String fromCurrency,
            String toCurrency,
            BigDecimal exchangeRate) {
        
        if (amount == null || exchangeRate == null) {
            return amount;
        }
        
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        
        BigDecimal converted = amount.multiply(exchangeRate);
        
        logger.debug("Converted {} {} to {} {}: {}", amount, fromCurrency, converted, toCurrency, exchangeRate);
        
        return converted.setScale(2, RoundingMode.HALF_UP);
    }
}
