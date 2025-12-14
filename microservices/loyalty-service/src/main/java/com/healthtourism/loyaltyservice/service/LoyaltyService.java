package com.healthtourism.loyaltyservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LoyaltyService {
    public Map<String, Object> getPoints(Long userId) {
        return Map.of("points", 1250, "level", "Gold");
    }

    public Map<String, Object> redeem(Long rewardId) {
        return Map.of("success", true, "rewardId", rewardId);
    }

    public List<Map<String, Object>> getRewards() {
        return new ArrayList<>();
    }

    public List<Map<String, Object>> getHistory(Long userId) {
        return new ArrayList<>();
    }
}

