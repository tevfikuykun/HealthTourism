package com.healthtourism.dashboard.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IntegrationStatus {
    private String name;
    private String category;
    private String url;
    private Integer port;
    private Status status;
    private String message;
    private LocalDateTime lastChecked;
    private Long responseTime; // milliseconds
    
    public enum Status {
        UP, DOWN, UNKNOWN
    }
}






