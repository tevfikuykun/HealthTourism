package com.healthtourism.kafkaservice.controller;

import com.healthtourism.kafkaservice.event.ReservationEvent;
import com.healthtourism.kafkaservice.service.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {
    
    @Autowired
    private EventProducer eventProducer;
    
    @PostMapping("/reservation")
    public ResponseEntity<String> publishReservationEvent(@RequestBody ReservationEvent event) {
        event.setTimestamp(LocalDateTime.now());
        eventProducer.sendReservationEvent(event);
        return ResponseEntity.ok("Reservation event published");
    }
    
    @PostMapping("/payment")
    public ResponseEntity<String> publishPaymentEvent(@RequestBody Object event) {
        eventProducer.sendPaymentEvent(event);
        return ResponseEntity.ok("Payment event published");
    }
    
    @PostMapping("/notification")
    public ResponseEntity<String> publishNotificationEvent(@RequestBody Object event) {
        eventProducer.sendNotificationEvent(event);
        return ResponseEntity.ok("Notification event published");
    }
    
    @PostMapping("/audit")
    public ResponseEntity<String> publishAuditEvent(@RequestBody Object event) {
        eventProducer.sendAuditEvent(event);
        return ResponseEntity.ok("Audit event published");
    }
}


