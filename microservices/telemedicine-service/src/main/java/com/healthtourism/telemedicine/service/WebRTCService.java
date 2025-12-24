package com.healthtourism.telemedicine.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebRTC Signaling Service
 * Handles WebRTC peer connection signaling
 */
@Service
public class WebRTCService {
    
    @Value("${webrtc.stun.server:stun:stun.l.google.com:19302}")
    private String stunServer;
    
    @Value("${webrtc.turn.server:}")
    private String turnServer;
    
    @Value("${webrtc.turn.username:}")
    private String turnUsername;
    
    @Value("${webrtc.turn.password:}")
    private String turnPassword;
    
    // Store active peer connections
    private final Map<String, PeerConnection> activeConnections = new ConcurrentHashMap<>();
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Create WebRTC offer
     */
    public Map<String, Object> createOffer(String roomId, String userId) {
        String offerId = UUID.randomUUID().toString();
        
        PeerConnection peerConnection = new PeerConnection();
        peerConnection.setRoomId(roomId);
        peerConnection.setUserId(userId);
        peerConnection.setOfferId(offerId);
        peerConnection.setStatus("OFFER_CREATED");
        
        activeConnections.put(offerId, peerConnection);
        
        Map<String, Object> response = new HashMap<>();
        response.put("offerId", offerId);
        response.put("roomId", roomId);
        response.put("iceServers", getIceServers());
        response.put("status", "OFFER_CREATED");
        
        return response;
    }
    
    /**
     * Create WebRTC answer
     */
    public Map<String, Object> createAnswer(String offerId, String userId) {
        PeerConnection peerConnection = activeConnections.get(offerId);
        if (peerConnection == null) {
            throw new RuntimeException("Offer not found");
        }
        
        String answerId = UUID.randomUUID().toString();
        peerConnection.setAnswerId(answerId);
        peerConnection.setAnswerUserId(userId);
        peerConnection.setStatus("ANSWER_CREATED");
        
        Map<String, Object> response = new HashMap<>();
        response.put("answerId", answerId);
        response.put("offerId", offerId);
        response.put("roomId", peerConnection.getRoomId());
        response.put("iceServers", getIceServers());
        response.put("status", "ANSWER_CREATED");
        
        return response;
    }
    
    /**
     * Add ICE candidate
     */
    public void addIceCandidate(String offerId, Map<String, Object> candidate) {
        PeerConnection peerConnection = activeConnections.get(offerId);
        if (peerConnection != null) {
            peerConnection.getIceCandidates().add(candidate);
        }
    }
    
    /**
     * Get ICE servers configuration
     */
    public java.util.List<Map<String, Object>> getIceServers() {
        java.util.List<Map<String, Object>> iceServers = new java.util.ArrayList<>();
        
        // STUN server
        Map<String, Object> stun = new HashMap<>();
        stun.put("urls", stunServer);
        iceServers.add(stun);
        
        // TURN server (if configured)
        if (turnServer != null && !turnServer.isEmpty()) {
            Map<String, Object> turn = new HashMap<>();
            turn.put("urls", turnServer);
            if (turnUsername != null && !turnUsername.isEmpty()) {
                turn.put("username", turnUsername);
            }
            if (turnPassword != null && !turnPassword.isEmpty()) {
                turn.put("credential", turnPassword);
            }
            iceServers.add(turn);
        }
        
        return iceServers;
    }
    
    /**
     * Get peer connection status
     */
    public Map<String, Object> getConnectionStatus(String offerId) {
        PeerConnection peerConnection = activeConnections.get(offerId);
        if (peerConnection == null) {
            throw new RuntimeException("Connection not found");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("offerId", offerId);
        response.put("roomId", peerConnection.getRoomId());
        response.put("status", peerConnection.getStatus());
        response.put("iceCandidates", peerConnection.getIceCandidates());
        
        return response;
    }
    
    /**
     * Close peer connection
     */
    public void closeConnection(String offerId) {
        activeConnections.remove(offerId);
    }
    
    /**
     * Peer Connection model
     */
    private static class PeerConnection {
        private String roomId;
        private String userId;
        private String offerId;
        private String answerId;
        private String answerUserId;
        private String status;
        private java.util.List<Map<String, Object>> iceCandidates = new java.util.ArrayList<>();
        
        // Getters and setters
        public String getRoomId() { return roomId; }
        public void setRoomId(String roomId) { this.roomId = roomId; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getOfferId() { return offerId; }
        public void setOfferId(String offerId) { this.offerId = offerId; }
        
        public String getAnswerId() { return answerId; }
        public void setAnswerId(String answerId) { this.answerId = answerId; }
        
        public String getAnswerUserId() { return answerUserId; }
        public void setAnswerUserId(String answerUserId) { this.answerUserId = answerUserId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public java.util.List<Map<String, Object>> getIceCandidates() { return iceCandidates; }
        public void setIceCandidates(java.util.List<Map<String, Object>> iceCandidates) { this.iceCandidates = iceCandidates; }
    }
}

