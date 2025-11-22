package com.healthtourism.monitoringservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceHealthDTO {
    private Long id;
    private String serviceName;
    private String status;
    private String message;
    private Long responseTime;
    private LocalDateTime checkedAt;
}

