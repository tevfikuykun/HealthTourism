package com.healthtourism.reservationservice.service;

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
    
    private static final String RESERVATION_TOPIC = "reservation-events";
    
    public void publishReservationCreated(Long reservationId, Long userId, Long hospitalId) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "CREATED");
        event.put("reservationId", reservationId);
        event.put("userId", userId);
        event.put("hospitalId", hospitalId);
        event.put("timestamp", System.currentTimeMillis());
        
        publishEvent(RESERVATION_TOPIC, event);
    }
    
    public void publishReservationUpdated(Long reservationId, String status) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "UPDATED");
        event.put("reservationId", reservationId);
        event.put("status", status);
        event.put("timestamp", System.currentTimeMillis());
        
        publishEvent(RESERVATION_TOPIC, event);
    }
    
    public void publishReservationCancelled(Long reservationId) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "CANCELLED");
        event.put("reservationId", reservationId);
        event.put("timestamp", System.currentTimeMillis());
        
        publishEvent(RESERVATION_TOPIC, event);
    }
    
    private void publishEvent(String topic, Map<String, Object> event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to publish event: " + e.getMessage());
        }
    }
}


