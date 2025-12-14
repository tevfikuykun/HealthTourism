package com.healthtourism.gdprservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class GDPRService {
    public byte[] exportData(Long userId) {
        return new byte[0];
    }

    public Map<String, Object> deleteAccount(Long userId) {
        return Map.of("deleted", true, "userId", userId);
    }
}

