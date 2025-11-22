package com.healthtourism.loggingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogEntryDTO {
    private Long id;
    private String serviceName;
    private String level;
    private String message;
    private String exception;
    private String userId;
    private String requestId;
    private LocalDateTime timestamp;
}

