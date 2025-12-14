package com.healthtourism.videoconsultationservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class VideoConsultationService {
    public Map<String, Object> start(Map<String, Object> consultation) {
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", UUID.randomUUID().toString());
        result.put("status", "started");
        result.put("roomUrl", "https://meet.example.com/" + UUID.randomUUID().toString());
        return result;
    }

    public Map<String, Object> end(String sessionId) {
        return Map.of("sessionId", sessionId, "status", "ended");
    }

    public List<Map<String, Object>> getSessions() {
        return new ArrayList<>();
    }
}

