package com.healthtourism.paymentservice.service;

import com.healthtourism.paymentservice.dto.PaymentDTO;
import com.healthtourism.paymentservice.dto.PaymentRequestDTO;
import com.healthtourism.paymentservice.entity.Payment;
import com.healthtourism.paymentservice.repository.PaymentRepository;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private KafkaEventService kafkaEventService;
    
    @Autowired
    private EventStoreService eventStoreService;
    
    @Autowired(required = false)
    private StripePaymentService stripePaymentService;
    
    @Autowired(required = false)
    private com.healthtourism.paymentservice.integration.PayPalPaymentService payPalPaymentService;
    
    @Transactional
    public PaymentDTO processPayment(PaymentRequestDTO request) {
        Payment payment = new Payment();
        payment.setPaymentNumber(UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        payment.setUserId(request.getUserId());
        payment.setReservationId(request.getReservationId());
        payment.setReservationType(request.getReservationType());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency() != null ? request.getCurrency() : "TRY");
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setCreatedAt(LocalDateTime.now());
        
        String status = "PENDING";
        String transactionId = null;
        
        // Process payment with Stripe if available
        if (stripePaymentService != null && "CREDIT_CARD".equals(request.getPaymentMethod())) {
            try {
                Map<String, String> metadata = new HashMap<>();
                metadata.put("userId", String.valueOf(request.getUserId()));
                metadata.put("reservationId", String.valueOf(request.getReservationId()));
                metadata.put("reservationType", request.getReservationType());
                
                PaymentIntent paymentIntent = stripePaymentService.createPaymentIntent(
                    request.getAmount(),
                    payment.getCurrency(),
                    request.getCardNumber(), // In real app, this would be payment method ID
                    metadata
                );
                
                transactionId = paymentIntent.getId();
                status = mapStripeStatus(paymentIntent.getStatus());
                
            } catch (Exception e) {
                status = "FAILED";
                payment.setNotes("Payment processing failed: " + e.getMessage());
            }
        } 
        // Process payment with PayPal if available
        else if (payPalPaymentService != null && "PAYPAL".equals(request.getPaymentMethod())) {
            try {
                Map<String, Object> orderResult = payPalPaymentService.createOrder(
                    request.getAmount(),
                    payment.getCurrency(),
                    "Health Tourism Payment - Reservation #" + request.getReservationId()
                );
                
                transactionId = (String) orderResult.get("orderId");
                status = "PENDING"; // PayPal requires user approval, so status is PENDING
                payment.setNotes("PayPal order created. Approval URL: " + orderResult.get("approvalUrl"));
                
            } catch (Exception e) {
                status = "FAILED";
                payment.setNotes("PayPal payment processing failed: " + e.getMessage());
            }
        } 
        else {
            // Fallback to simulation
            status = "COMPLETED";
            transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase();
        }
        
        payment.setStatus(status);
        payment.setTransactionId(transactionId);
        payment.setPaymentDate(LocalDateTime.now());
        
        Payment saved = paymentRepository.save(payment);
        
        // Save event to event store
        com.healthtourism.paymentservice.event.PaymentEvent event = 
            new com.healthtourism.paymentservice.event.PaymentEvent();
        event.setEventType("PAYMENT_" + saved.getStatus());
        event.setPaymentId(saved.getId());
        event.setPaymentNumber(saved.getPaymentNumber());
        event.setUserId(saved.getUserId());
        event.setReservationId(saved.getReservationId());
        event.setReservationType(saved.getReservationType());
        event.setAmount(saved.getAmount());
        event.setCurrency(saved.getCurrency());
        event.setPaymentMethod(saved.getPaymentMethod());
        event.setStatus(saved.getStatus());
        event.setTransactionId(saved.getTransactionId());
        event.setPaymentDate(saved.getPaymentDate());
        event.setEventTimestamp(java.time.LocalDateTime.now());
        event.setEventId(java.util.UUID.randomUUID().toString());
        
        eventStoreService.saveEvent("PAYMENT_" + saved.getStatus(), saved.getId(), event, 1L);
        
        // Publish event to Kafka
        if ("COMPLETED".equals(saved.getStatus())) {
            kafkaEventService.publishPaymentCompleted(saved.getId(), saved.getTransactionId());
        } else {
            kafkaEventService.publishPaymentCreated(saved.getId(), saved.getReservationId(), saved.getStatus(), saved.getAmount());
        }
        
        return convertToDTO(saved);
    }
    
    public List<PaymentDTO> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public PaymentDTO getPaymentByNumber(String paymentNumber) {
        Payment payment = paymentRepository.findByPaymentNumber(paymentNumber)
                .orElseThrow(() -> new RuntimeException("Ödeme bulunamadı"));
        return convertToDTO(payment);
    }
    
    @Transactional
    public PaymentDTO refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Ödeme bulunamadı"));
        
        if (!"COMPLETED".equals(payment.getStatus())) {
            throw new RuntimeException("Sadece tamamlanmış ödemeler iade edilebilir");
        }
        
        payment.setStatus("REFUNDED");
        Payment saved = paymentRepository.save(payment);
        
        // Save event to event store
        com.healthtourism.paymentservice.event.PaymentEvent event = 
            new com.healthtourism.paymentservice.event.PaymentEvent();
        event.setEventType("PAYMENT_REFUNDED");
        event.setPaymentId(saved.getId());
        event.setPaymentNumber(saved.getPaymentNumber());
        event.setStatus(saved.getStatus());
        event.setEventTimestamp(java.time.LocalDateTime.now());
        event.setEventId(java.util.UUID.randomUUID().toString());
        
        // Get current version from event store
        java.util.List<com.healthtourism.paymentservice.entity.PaymentEventStore> events = 
            eventStoreService.getEventsByPaymentId(saved.getId());
        Long version = events.stream()
            .mapToLong(e -> e.getAggregateVersion())
            .max()
            .orElse(0L) + 1;
        eventStoreService.saveEvent("PAYMENT_REFUNDED", saved.getId(), event, version);
        
        // Publish event to Kafka
        kafkaEventService.publishPaymentRefunded(saved.getId());
        
        return convertToDTO(saved);
    }
    
    private String mapStripeStatus(String stripeStatus) {
        switch (stripeStatus) {
            case "succeeded":
                return "COMPLETED";
            case "requires_payment_method":
            case "requires_confirmation":
            case "requires_action":
                return "PENDING";
            case "canceled":
                return "CANCELLED";
            default:
                return "FAILED";
        }
    }
    
    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setPaymentNumber(payment.getPaymentNumber());
        dto.setUserId(payment.getUserId());
        dto.setReservationId(payment.getReservationId());
        dto.setReservationType(payment.getReservationType());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setNotes(payment.getNotes());
        dto.setTransactionId(payment.getTransactionId());
        return dto;
    }
}

