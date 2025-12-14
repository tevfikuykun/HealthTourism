package com.healthtourism.livechatservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column
    private Long agentId;
    @Column(nullable = false)
    private String status; // ACTIVE, WAITING, CLOSED
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime closedAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "WAITING";
    }
}

