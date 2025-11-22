package com.healthtourism.paymentservice.service;

import com.healthtourism.paymentservice.dto.PaymentDTO;
import com.healthtourism.paymentservice.dto.PaymentRequestDTO;
import com.healthtourism.paymentservice.entity.Payment;
import com.healthtourism.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Transactional
    public PaymentDTO processPayment(PaymentRequestDTO request) {
        // Ödeme işlemi simülasyonu (Gerçek uygulamada Stripe/PayPal entegrasyonu olacak)
        Payment payment = new Payment();
        payment.setPaymentNumber(UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        payment.setUserId(request.getUserId());
        payment.setReservationId(request.getReservationId());
        payment.setReservationType(request.getReservationType());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency() != null ? request.getCurrency() : "TRY");
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("COMPLETED"); // Simülasyon - gerçekte ödeme gateway'den gelecek
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase());
        
        Payment saved = paymentRepository.save(payment);
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
        return convertToDTO(saved);
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

