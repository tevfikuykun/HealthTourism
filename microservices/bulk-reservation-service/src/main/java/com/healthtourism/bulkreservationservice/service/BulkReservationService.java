package com.healthtourism.bulkreservationservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BulkReservationService {
    public Map<String, Object> create(Map<String, Object> reservation) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", UUID.randomUUID().toString());
        result.put("status", "created");
        result.put("reservations", reservation.get("reservations"));
        return result;
    }

    public Map<String, Object> getById(String id) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("status", "completed");
        return result;
    }
}

