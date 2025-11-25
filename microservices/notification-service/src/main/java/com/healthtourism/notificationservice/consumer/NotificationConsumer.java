package com.healthtourism.notificationservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthtourism.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @KafkaListener(topics = "reservation-events", groupId = "notification-service-group")
    public void consumeReservationEvent(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            String eventType = (String) event.get("eventType");
            Long userId = ((Number) event.get("userId")).longValue();
            
            if ("CREATED".equals(eventType)) {
                notificationService.sendReservationCreatedNotification(userId, event);
            } else if ("UPDATED".equals(eventType)) {
                notificationService.sendReservationUpdatedNotification(userId, event);
            } else if ("CANCELLED".equals(eventType)) {
                notificationService.sendReservationCancelledNotification(userId, event);
            }
        } catch (Exception e) {
            logger.error("Error processing reservation event: {}", e.getMessage());
        }
    }
    
    @KafkaListener(topics = "payment-events", groupId = "notification-service-group")
    public void consumePaymentEvent(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            String eventType = (String) event.get("eventType");
            Long userId = ((Number) event.get("userId")).longValue();
            
            if ("COMPLETED".equals(eventType)) {
                notificationService.sendPaymentCompletedNotification(userId, event);
            } else if ("FAILED".equals(eventType)) {
                notificationService.sendPaymentFailedNotification(userId, event);
            }
        } catch (Exception e) {
            logger.error("Error processing payment event: {}", e.getMessage());
        }
    }
}


