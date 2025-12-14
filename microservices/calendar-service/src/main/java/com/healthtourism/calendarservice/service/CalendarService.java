package com.healthtourism.calendarservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CalendarService {
    public List<Map<String, Object>> getAppointments(Map<String, Object> params) {
        return new ArrayList<>();
    }

    public Map<String, Object> checkConflict(Map<String, Object> appointment) {
        return Map.of("hasConflict", false);
    }

    public List<Map<String, Object>> getUpcoming() {
        return new ArrayList<>();
    }
}

