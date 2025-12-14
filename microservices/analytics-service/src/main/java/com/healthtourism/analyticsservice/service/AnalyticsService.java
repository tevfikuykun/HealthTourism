package com.healthtourism.analyticsservice.service;

import com.healthtourism.analyticsservice.entity.AnalyticsData;
import com.healthtourism.analyticsservice.repository.AnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    public Map<String, Object> getRevenue(String period) {
        List<AnalyticsData> data = analyticsRepository.findByMetricTypeAndPeriod("revenue", period);
        return buildResponse(data);
    }

    public Map<String, Object> getUsers(String period) {
        List<AnalyticsData> data = analyticsRepository.findByMetricTypeAndPeriod("users", period);
        return buildResponse(data);
    }

    public Map<String, Object> getReservations(String period) {
        List<AnalyticsData> data = analyticsRepository.findByMetricTypeAndPeriod("reservations", period);
        return buildResponse(data);
    }

    public Map<String, Object> getServices(String period) {
        List<AnalyticsData> data = analyticsRepository.findByMetricTypeAndPeriod("services", period);
        return buildResponse(data);
    }

    private Map<String, Object> buildResponse(List<AnalyticsData> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("total", data.stream().mapToDouble(AnalyticsData::getValue).sum());
        response.put("count", data.size());
        return response;
    }
}

