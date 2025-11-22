package com.healthtourism.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {
    private Long userId;
    private String type; // EMAIL, SMS, PUSH
    private String category;
    private String subject;
    private String message;
    private String recipient;
}

