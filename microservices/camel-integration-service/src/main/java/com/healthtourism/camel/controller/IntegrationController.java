package com.healthtourism.camel.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Integration Controller
 * REST endpoints for manual integration triggers
 */
@RestController
@RequestMapping("/api/integration")
public class IntegrationController {
    
    @Autowired
    private CamelContext camelContext;
    
    @Autowired
    private ProducerTemplate producerTemplate;
    
    @PostMapping("/trigger/{routeId}")
    public ResponseEntity<Map<String, Object>> triggerRoute(
            @PathVariable String routeId,
            @RequestBody Map<String, Object> data) {
        
        try {
            // Send message to route
            producerTemplate.sendBody("direct:" + routeId, data);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "routeId", routeId,
                "message", "Route triggered successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/routes")
    public ResponseEntity<Map<String, Object>> getRoutes() {
        Map<String, Object> routes = new HashMap<>();
        camelContext.getRoutes().forEach(route -> {
            routes.put(route.getId(), Map.of(
                "status", camelContext.getRouteStatus(route.getId()).name(),
                "uptime", camelContext.getRouteController().getRouteStatus(route.getId())
            ));
        });
        
        return ResponseEntity.ok(Map.of(
            "totalRoutes", camelContext.getRoutes().size(),
            "routes", routes
        ));
    }
    
    @PostMapping("/route/{routeId}/start")
    public ResponseEntity<Map<String, Object>> startRoute(@PathVariable String routeId) {
        try {
            camelContext.getRouteController().startRoute(routeId);
            return ResponseEntity.ok(Map.of("status", "started", "routeId", routeId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/route/{routeId}/stop")
    public ResponseEntity<Map<String, Object>> stopRoute(@PathVariable String routeId) {
        try {
            camelContext.getRouteController().stopRoute(routeId);
            return ResponseEntity.ok(Map.of("status", "stopped", "routeId", routeId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}






