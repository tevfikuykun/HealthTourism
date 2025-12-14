package com.healthtourism.securityalertsservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SecurityAlertsService {
    public List<Map<String, Object>> getAll(Map<String, Object> params) {
        return new ArrayList<>();
    }

    public Map<String, Object> create(Map<String, Object> alert) {
        Map<String, Object> result = new HashMap<>(alert);
        result.put("id", UUID.randomUUID().toString());
        result.put("createdAt", new Date());
        return result;
    }

    public Map<String, Object> updatePreferences(Map<String, Object> preferences) {
        return Map.of("updated", true);
    }
}

