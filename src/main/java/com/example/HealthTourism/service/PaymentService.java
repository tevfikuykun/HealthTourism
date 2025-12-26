package com.example.HealthTourism.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Enterprise-grade PaymentService with:
 * - Mock payment processing (for development/testing)
 * - Invoice number generation
 * - Payment validation
 * 
 * Health Tourism Critical: Payments are often partial (deposit + hospital payment).
 * This is a mock implementation. In production, integrate with payment gateways
 * like Iyzico, Stripe, or PayPal.
 * 
 * Note: For production, this service should integrate with actual payment gateways
 * and implement proper error handling, retry mechanisms, and webhook handling.
 */
@Slf4j
@Service
@RequiredArgsConstructor // Constructor Injection - Spring best practice
@Transactional(readOnly = true) // Default: read-only for all methods
public class PaymentService {
    
    // Business constants
    private static final int MIN_CARD_NUMBER_LENGTH = 13; // Minimum valid card number length
    private static final int MAX_CARD_NUMBER_LENGTH = 19; // Maximum valid card number length
    private static final String INVOICE_PREFIX = "INV";
    private static final String INVOICE_DATE_FORMAT = "yyyyMMdd";

    /**
     * Processes payment (Mock implementation).
     * 
     * Health Tourism Business Logic:
     * - Payments can be partial (deposit + final payment)
     * - Credit card validation (basic checks)
     * - In production, this would call payment gateway API (Iyzico, Stripe, etc.)
     * 
     * @param userId User ID making the payment
     * @param amount Payment amount
     * @param cardNumber Credit card number (should be encrypted in production)
     * @param reservationId Reservation ID (optional)
     * @return true if payment processed successfully
     * @throws IllegalArgumentException if payment parameters are invalid
     */
    @Transactional // Override read-only for write operation
    public boolean processPayment(Long userId, BigDecimal amount, String cardNumber, Long reservationId) {
        log.info("Processing payment - User ID: {}, Amount: {}, Reservation ID: {}", userId, amount, reservationId);
        
        // Business logic validation
        validatePaymentRequest(userId, amount, cardNumber);
        
        // Mock payment processing
        // In production, this would:
        // 1. Encrypt card number
        // 2. Call payment gateway API (Iyzico, Stripe, PayPal, etc.)
        // 3. Handle response (success/failure)
        // 4. Store transaction ID
        // 5. Update reservation status
        // 6. Send confirmation email
        
        boolean success = simulatePaymentProcessing(cardNumber);
        
        if (success) {
            String invoiceNumber = generateInvoiceNumber();
            log.info("Payment processed successfully - Invoice: {}, User ID: {}, Amount: {}", 
                    invoiceNumber, userId, amount);
            // TODO: Save payment to database
            // TODO: Update reservation status
            // TODO: Send payment confirmation email
            return true;
        } else {
            log.warn("Payment processing failed - User ID: {}, Amount: {}", userId, amount);
            throw new RuntimeException("Ödeme işlemi başarısız oldu. Lütfen kart bilgilerinizi kontrol edin.");
        }
    }
    
    /**
     * Processes payment (simple version without reservation ID).
     */
    @Transactional
    public boolean processPayment(Long userId, BigDecimal amount, String cardNumber) {
        return processPayment(userId, amount, cardNumber, null);
    }
    
    /**
     * Generates unique invoice number.
     * Format: INV-YYYYMMDD-XXXXXXXX
     * Example: INV-20250101-A1B2C3D4
     * 
     * @return Invoice number string
     */
    public String generateInvoiceNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern(INVOICE_DATE_FORMAT));
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("%s-%s-%s", INVOICE_PREFIX, datePart, randomPart);
    }
    
    /**
     * Validates payment request parameters.
     * 
     * @param userId User ID
     * @param amount Payment amount
     * @param cardNumber Credit card number
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private void validatePaymentRequest(Long userId, BigDecimal amount, String cardNumber) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Geçersiz kullanıcı ID'si.");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Ödeme tutarı pozitif bir değer olmalıdır. Verilen: " + amount);
        }
        
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Kart numarası gereklidir.");
        }
        
        // Basic card number validation (remove spaces and hyphens)
        String cleanedCardNumber = cardNumber.replaceAll("[\\s-]", "");
        
        if (cleanedCardNumber.length() < MIN_CARD_NUMBER_LENGTH || 
            cleanedCardNumber.length() > MAX_CARD_NUMBER_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Kart numarası %d-%d karakter arasında olmalıdır.", 
                            MIN_CARD_NUMBER_LENGTH, MAX_CARD_NUMBER_LENGTH));
        }
        
        // Basic Luhn algorithm check (optional, for basic validation)
        if (!isValidCardNumber(cleanedCardNumber)) {
            throw new IllegalArgumentException("Geçersiz kart numarası.");
        }
    }
    
    /**
     * Simulates payment processing (Mock).
     * In production, this would call actual payment gateway.
     * 
     * @param cardNumber Credit card number
     * @return true if payment successful, false otherwise
     */
    private boolean simulatePaymentProcessing(String cardNumber) {
        // Mock payment processing logic
        // In production, this would:
        // - Encrypt card number
        // - Call payment gateway API
        // - Handle 3D Secure if required
        // - Process response
        
        // Simple mock: Card number must be valid length
        String cleanedCardNumber = cardNumber.replaceAll("[\\s-]", "");
        
        // Simulate: Cards starting with "4" (Visa) or "5" (Mastercard) are accepted
        // This is just for mock purposes
        if (cleanedCardNumber.startsWith("4") || cleanedCardNumber.startsWith("5")) {
            log.debug("Mock payment processing: Card accepted");
            return true;
        }
        
        log.debug("Mock payment processing: Card rejected");
        return false;
    }
    
    /**
     * Validates credit card number using Luhn algorithm.
     * This is a basic validation - not a complete security check.
     * 
     * @param cardNumber Credit card number (without spaces/hyphens)
     * @return true if card number passes Luhn check
     */
    private boolean isValidCardNumber(String cardNumber) {
        try {
            int sum = 0;
            boolean alternate = false;
            
            // Process digits from right to left
            for (int i = cardNumber.length() - 1; i >= 0; i--) {
                int digit = Integer.parseInt(cardNumber.substring(i, i + 1));
                
                if (alternate) {
                    digit *= 2;
                    if (digit > 9) {
                        digit = (digit % 10) + 1;
                    }
                }
                
                sum += digit;
                alternate = !alternate;
            }
            
            return (sum % 10 == 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Calculates partial payment amount (deposit).
     * Health Tourism Business Logic: Typically 30% deposit required.
     * 
     * @param totalAmount Total amount
     * @param depositPercentage Deposit percentage (e.g., 30 for 30%)
     * @return Deposit amount
     */
    public BigDecimal calculateDeposit(BigDecimal totalAmount, BigDecimal depositPercentage) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Toplam tutar pozitif bir değer olmalıdır.");
        }
        
        if (depositPercentage == null || 
            depositPercentage.compareTo(BigDecimal.ZERO) <= 0 || 
            depositPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Depozito yüzdesi 0-100 arasında olmalıdır.");
        }
        
        return totalAmount.multiply(depositPercentage)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Calculates standard deposit (30%).
     * 
     * @param totalAmount Total amount
     * @return Deposit amount (30% of total)
     */
    public BigDecimal calculateStandardDeposit(BigDecimal totalAmount) {
        return calculateDeposit(totalAmount, BigDecimal.valueOf(30));
    }
}

