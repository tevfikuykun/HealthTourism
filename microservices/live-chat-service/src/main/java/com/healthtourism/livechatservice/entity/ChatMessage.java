package com.healthtourism.livechatservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column
    private Long agentId;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    @Column(nullable = false)
    private String senderType; // USER, AGENT, SYSTEM
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @Column
    private Boolean isRead;
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
        if (isRead == null) isRead = false;
    }
}

