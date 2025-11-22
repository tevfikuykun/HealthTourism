package com.healthtourism.monitoringservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricDTO {
    private Long id;
    private String serviceName;
    private String metricName;
    private Double value;
    private String unit;
    private LocalDateTime recordedAt;
}

