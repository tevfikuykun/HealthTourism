package com.healthtourism.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaEventService {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String PAYMENT_TOPIC = "payment-events";
    
    public void publishPaymentCreated(Long paymentId, Long reservationId, String status, Double amount) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "CREATED");
        event.put("paymentId", paymentId);
        event.put("reservationId", reservationId);
        event.put("status", status);
        event.put("amount", amount);
        event.put("timestamp", System.currentTimeMillis());
        
        publishEvent(PAYMENT_TOPIC, event);
    }
    
    public void publishPaymentCompleted(Long paymentId, String transactionId) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "COMPLETED");
        event.put("paymentId", paymentId);
        event.put("transactionId", transactionId);
        event.put("timestamp", System.currentTimeMillis());
        
        publishEvent(PAYMENT_TOPIC, event);
    }
    
    public void publishPaymentFailed(Long paymentId, String reason) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "FAILED");
        event.put("paymentId", paymentId);
        event.put("reason", reason);
        event.put("timestamp", System.currentTimeMillis());
        
        publishEvent(PAYMENT_TOPIC, event);
    }
    
    public void publishPaymentRefunded(Long paymentId) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "REFUNDED");
        event.put("paymentId", paymentId);
        event.put("timestamp", System.currentTimeMillis());
        
        publishEvent(PAYMENT_TOPIC, event);
    }
    
    private void publishEvent(String topic, Map<String, Object> event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            System.err.println("Failed to publish event: " + e.getMessage());
        }
    }
}

