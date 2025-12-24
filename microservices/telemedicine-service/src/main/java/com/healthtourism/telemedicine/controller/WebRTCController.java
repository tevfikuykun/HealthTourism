package com.healthtourism.telemedicine.controller;

import com.healthtourism.telemedicine.service.WebRTCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/telemedicine/webrtc")
@CrossOrigin(origins = "*")
public class WebRTCController {
    
    @Autowired
    private WebRTCService webRTCService;
    
    @PostMapping("/offer")
    public ResponseEntity<Map<String, Object>> createOffer(
            @RequestBody Map<String, String> request) {
        String roomId = request.get("roomId");
        String userId = request.get("userId");
        return ResponseEntity.ok(webRTCService.createOffer(roomId, userId));
    }
    
    @PostMapping("/answer")
    public ResponseEntity<Map<String, Object>> createAnswer(
            @RequestBody Map<String, String> request) {
        String offerId = request.get("offerId");
        String userId = request.get("userId");
        return ResponseEntity.ok(webRTCService.createAnswer(offerId, userId));
    }
    
    @PostMapping("/ice-candidate")
    public ResponseEntity<Map<String, Object>> addIceCandidate(
            @RequestBody Map<String, Object> request) {
        String offerId = (String) request.get("offerId");
        Map<String, Object> candidate = (Map<String, Object>) request.get("candidate");
        webRTCService.addIceCandidate(offerId, candidate);
        return ResponseEntity.ok(Map.of("status", "success"));
    }
    
    @GetMapping("/ice-servers")
    public ResponseEntity<Map<String, Object>> getIceServers() {
        return ResponseEntity.ok(Map.of("iceServers", webRTCService.getIceServers()));
    }
    
    @GetMapping("/status/{offerId}")
    public ResponseEntity<Map<String, Object>> getConnectionStatus(@PathVariable String offerId) {
        return ResponseEntity.ok(webRTCService.getConnectionStatus(offerId));
    }
    
    @PostMapping("/close/{offerId}")
    public ResponseEntity<Map<String, Object>> closeConnection(@PathVariable String offerId) {
        webRTCService.closeConnection(offerId);
        return ResponseEntity.ok(Map.of("status", "closed"));
    }
}

