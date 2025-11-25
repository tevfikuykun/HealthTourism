package com.healthtourism.reservationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthtourism.reservationservice.entity.ReservationEventStore;
import com.healthtourism.reservationservice.repository.ReservationEventStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EventStoreService {
    
    @Autowired
    private ReservationEventStoreRepository eventStoreRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Transactional
    public void saveEvent(String eventType, Long reservationId, Object eventData, Long aggregateVersion) {
        try {
            ReservationEventStore eventStore = new ReservationEventStore();
            eventStore.setEventId(UUID.randomUUID().toString());
            eventStore.setEventType(eventType);
            eventStore.setReservationId(reservationId);
            eventStore.setEventData(objectMapper.writeValueAsString(eventData));
            eventStore.setEventTimestamp(LocalDateTime.now());
            eventStore.setAggregateType("RESERVATION");
            eventStore.setAggregateVersion(aggregateVersion);
            
            eventStoreRepository.save(eventStore);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save event to event store", e);
        }
    }
    
    public java.util.List<ReservationEventStore> getEventsByReservationId(Long reservationId) {
        return eventStoreRepository.findByReservationIdOrderByEventTimestampAsc(reservationId);
    }
}

