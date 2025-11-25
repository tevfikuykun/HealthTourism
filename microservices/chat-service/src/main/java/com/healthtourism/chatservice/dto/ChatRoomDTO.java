package com.healthtourism.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Long id;
    private Long participant1Id;
    private Long participant2Id;
    private String participant1Type;
    private String participant2Type;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private Long unreadCount;
}

