package com.healthtourism.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthtourism.paymentservice.entity.PaymentEventStore;
import com.healthtourism.paymentservice.repository.PaymentEventStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EventStoreService {
    
    @Autowired
    private PaymentEventStoreRepository eventStoreRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Transactional
    public void saveEvent(String eventType, Long paymentId, Object eventData, Long aggregateVersion) {
        try {
            PaymentEventStore eventStore = new PaymentEventStore();
            eventStore.setEventId(UUID.randomUUID().toString());
            eventStore.setEventType(eventType);
            eventStore.setPaymentId(paymentId);
            eventStore.setEventData(objectMapper.writeValueAsString(eventData));
            eventStore.setEventTimestamp(LocalDateTime.now());
            eventStore.setAggregateType("PAYMENT");
            eventStore.setAggregateVersion(aggregateVersion);
            
            eventStoreRepository.save(eventStore);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save event to event store", e);
        }
    }
    
    public java.util.List<PaymentEventStore> getEventsByPaymentId(Long paymentId) {
        return eventStoreRepository.findByPaymentIdOrderByEventTimestampAsc(paymentId);
    }
}

