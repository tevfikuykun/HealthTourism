package com.healthtourism.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String type;
    private String category;
    private String subject;
    private String message;
    private String recipient;
    private String status;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}

