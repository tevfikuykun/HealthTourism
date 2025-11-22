package com.healthtourism.monitoringservice.service;

import com.healthtourism.monitoringservice.dto.MetricDTO;
import com.healthtourism.monitoringservice.dto.ServiceHealthDTO;
import com.healthtourism.monitoringservice.entity.Metric;
import com.healthtourism.monitoringservice.entity.ServiceHealth;
import com.healthtourism.monitoringservice.repository.MetricRepository;
import com.healthtourism.monitoringservice.repository.ServiceHealthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MonitoringService {
    
    @Autowired
    private ServiceHealthRepository healthRepository;
    
    @Autowired
    private MetricRepository metricRepository;
    
    public ServiceHealthDTO recordHealthCheck(String serviceName, String status, String message, Long responseTime) {
        ServiceHealth health = new ServiceHealth();
        health.setServiceName(serviceName);
        health.setStatus(status);
        health.setMessage(message);
        health.setResponseTime(responseTime);
        health = healthRepository.save(health);
        return convertToDTO(health);
    }
    
    public ServiceHealthDTO getLatestHealth(String serviceName) {
        return healthRepository.findFirstByServiceNameOrderByCheckedAtDesc(serviceName)
            .map(this::convertToDTO)
            .orElse(null);
    }
    
    public List<ServiceHealthDTO> getHealthHistory(String serviceName) {
        return healthRepository.findByServiceNameOrderByCheckedAtDesc(serviceName)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public MetricDTO recordMetric(String serviceName, String metricName, Double value, String unit) {
        Metric metric = new Metric();
        metric.setServiceName(serviceName);
        metric.setMetricName(metricName);
        metric.setValue(value);
        metric.setUnit(unit);
        metric = metricRepository.save(metric);
        return convertToDTO(metric);
    }
    
    public List<MetricDTO> getMetrics(String serviceName, String metricName, LocalDateTime start, LocalDateTime end) {
        return metricRepository.findByServiceNameAndMetricNameAndRecordedAtBetween(serviceName, metricName, start, end)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<MetricDTO> getAllMetrics(String serviceName) {
        return metricRepository.findByServiceNameOrderByRecordedAtDesc(serviceName)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private ServiceHealthDTO convertToDTO(ServiceHealth health) {
        return new ServiceHealthDTO(
            health.getId(),
            health.getServiceName(),
            health.getStatus(),
            health.getMessage(),
            health.getResponseTime(),
            health.getCheckedAt()
        );
    }
    
    private MetricDTO convertToDTO(Metric metric) {
        return new MetricDTO(
            metric.getId(),
            metric.getServiceName(),
            metric.getMetricName(),
            metric.getValue(),
            metric.getUnit(),
            metric.getRecordedAt()
        );
    }
}

