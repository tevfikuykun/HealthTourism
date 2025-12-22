package com.healthtourism.keptn;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Keptn Event Service
 * Handles self-healing events and stores them for dashboard
 */
@Service
public class KeptnEventService {
    
    @Value("${keptn.api.url:http://localhost:8080}")
    private String keptnApiUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private SelfHealingEventRepository eventRepository;
    
    /**
     * Record auto-fix event
     */
    public void recordAutoFixed(String serviceName, String issue, String fix) {
        SelfHealingEvent event = new SelfHealingEvent();
        event.setId(UUID.randomUUID().toString());
        event.setType("AUTO_FIXED");
        event.setServiceName(serviceName);
        event.setAction(fix);
        event.setDescription(issue);
        event.setStatus("SUCCESS");
        event.setTimestamp(LocalDateTime.now());
        event.setResponseTimeMs(calculateResponseTime());
        
        eventRepository.save(event);
        
        // Send to Keptn
        sendKeptnEvent("sh.keptn.event.problem.auto-resolved", event);
    }
    
    /**
     * Record auto-scale event
     */
    public void recordAutoScaled(String serviceName, String direction, int instances) {
        SelfHealingEvent event = new SelfHealingEvent();
        event.setId(UUID.randomUUID().toString());
        event.setType("AUTO_SCALED");
        event.setServiceName(serviceName);
        event.setAction(String.format("Scaled %s to %d instances", direction, instances));
        event.setDescription("CPU/Memory threshold exceeded");
        event.setStatus("SUCCESS");
        event.setTimestamp(LocalDateTime.now());
        
        eventRepository.save(event);
        
        sendKeptnEvent("sh.keptn.event.scaling.triggered", event);
    }
    
    /**
     * Record auto-rollback event
     */
    public void recordAutoRollback(String serviceName, String reason) {
        SelfHealingEvent event = new SelfHealingEvent();
        event.setId(UUID.randomUUID().toString());
        event.setType("AUTO_ROLLBACK");
        event.setServiceName(serviceName);
        event.setAction("Rolled back to previous version");
        event.setDescription(reason);
        event.setStatus("SUCCESS");
        event.setTimestamp(LocalDateTime.now());
        
        eventRepository.save(event);
        
        sendKeptnEvent("sh.keptn.event.deployment.rollback", event);
    }
    
    /**
     * Record auto-restart event
     */
    public void recordAutoRestart(String serviceName, String reason) {
        SelfHealingEvent event = new SelfHealingEvent();
        event.setId(UUID.randomUUID().toString());
        event.setType("AUTO_RESTART");
        event.setServiceName(serviceName);
        event.setAction("Restarted service");
        event.setDescription(reason);
        event.setStatus("SUCCESS");
        event.setTimestamp(LocalDateTime.now());
        
        eventRepository.save(event);
        
        sendKeptnEvent("sh.keptn.event.service.restart", event);
    }
    
    private void sendKeptnEvent(String eventType, SelfHealingEvent event) {
        try {
            Map<String, Object> keptnEvent = new HashMap<>();
            keptnEvent.put("type", eventType);
            keptnEvent.put("source", "healthtourism");
            keptnEvent.put("data", Map.of(
                "service", event.getServiceName(),
                "action", event.getAction(),
                "status", event.getStatus()
            ));
            
            // In production, this would send to Keptn API
            // restTemplate.postForEntity(keptnApiUrl + "/api/v1/event", keptnEvent, String.class);
        } catch (Exception e) {
            // Log error but don't fail
            System.err.println("Failed to send Keptn event: " + e.getMessage());
        }
    }
    
    private long calculateResponseTime() {
        // Simulate response time calculation
        return (long) (Math.random() * 1000 + 100);
    }
}






