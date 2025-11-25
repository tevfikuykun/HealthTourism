package com.healthtourism.kafkaservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @KafkaListener(topics = "${kafka.topics.reservation}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeReservationEvent(String message) {
        logger.info("Received reservation event: {}", message);
        // Process reservation event
    }
    
    @KafkaListener(topics = "${kafka.topics.payment}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaymentEvent(String message) {
        logger.info("Received payment event: {}", message);
        // Process payment event
    }
    
    @KafkaListener(topics = "${kafka.topics.notification}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeNotificationEvent(String message) {
        logger.info("Received notification event: {}", message);
        // Process notification event
    }
    
    @KafkaListener(topics = "${kafka.topics.audit}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAuditEvent(String message) {
        logger.info("Received audit event: {}", message);
        // Process audit event
    }
}


