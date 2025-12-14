package com.healthtourism.analyticsservice.repository;

import com.healthtourism.analyticsservice.entity.AnalyticsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<AnalyticsData, Long> {
    List<AnalyticsData> findByMetricTypeAndPeriod(String metricType, String period);
    
    @Query("SELECT a FROM AnalyticsData a WHERE a.metricType = :metricType AND a.period = :period AND a.date >= :startDate AND a.date <= :endDate ORDER BY a.date")
    List<AnalyticsData> findByMetricTypeAndPeriodAndDateBetween(
            @Param("metricType") String metricType,
            @Param("period") String period,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}

