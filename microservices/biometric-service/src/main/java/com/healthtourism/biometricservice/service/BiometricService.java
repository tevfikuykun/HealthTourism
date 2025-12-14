package com.healthtourism.biometricservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BiometricService {
    public Map<String, Object> register(Map<String, Object> data) {
        return Map.of("registered", true, "biometricId", UUID.randomUUID().toString());
    }

    public Map<String, Object> verify(Map<String, Object> data) {
        return Map.of("verified", true);
    }
}

