package com.healthtourism.flink.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * IoT Event Model
 * Represents patient monitoring data from IoT devices
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IoTEvent {
    private Long userId;
    private Long deviceId;
    private LocalDateTime timestamp;
    private Double heartRate;
    private Double temperature;
    private Double bloodPressureSystolic;
    private Double bloodPressureDiastolic;
    private String location;
    private Double latitude;
    private Double longitude;
}






