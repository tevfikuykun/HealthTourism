package com.healthtourism.twofactorservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TwoFactorService {
    public Map<String, Object> enable(String method, Map<String, Object> data) {
        return Map.of("enabled", true, "method", method);
    }

    public Map<String, Object> verify(String code) {
        return Map.of("verified", true);
    }

    public Map<String, Object> disable() {
        return Map.of("disabled", true);
    }

    public Map<String, Object> getStatus() {
        return Map.of("enabled", false, "method", "");
    }
}

