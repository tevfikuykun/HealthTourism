package com.healthtourism.monitoringservice.repository;

import com.healthtourism.monitoringservice.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MetricRepository extends JpaRepository<Metric, Long> {
    List<Metric> findByServiceNameAndMetricNameAndRecordedAtBetween(
        String serviceName, String metricName, LocalDateTime start, LocalDateTime end
    );
    List<Metric> findByServiceNameOrderByRecordedAtDesc(String serviceName);
}

