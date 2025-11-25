package com.healthtourism.kafkaservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${kafka.topics.reservation}")
    private String reservationTopic;
    
    @Value("${kafka.topics.payment}")
    private String paymentTopic;
    
    @Value("${kafka.topics.notification}")
    private String notificationTopic;
    
    @Value("${kafka.topics.audit}")
    private String auditTopic;
    
    public void sendReservationEvent(Object event) {
        sendEvent(reservationTopic, event);
    }
    
    public void sendPaymentEvent(Object event) {
        sendEvent(paymentTopic, event);
    }
    
    public void sendNotificationEvent(Object event) {
        sendEvent(notificationTopic, event);
    }
    
    public void sendAuditEvent(Object event) {
        sendEvent(auditTopic, event);
    }
    
    private void sendEvent(String topic, Object event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing event", e);
        }
    }
}


