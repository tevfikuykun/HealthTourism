package com.healthtourism.reminder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Reminder Entity
 * Tracks reminders for quotes, leads, and other business events
 */
@Entity
@Table(name = "reminders", indexes = {
    @Index(name = "idx_reminder_type", columnList = "reminderType"),
    @Index(name = "idx_scheduled_at", columnList = "scheduledAt"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReminderType reminderType;
    
    @Column(nullable = false)
    private Long entityId; // Quote ID, Lead ID, etc.
    
    @Column(nullable = false)
    private Long userId; // User to remind
    
    @Column(nullable = false)
    private String userEmail;
    
    @Column(nullable = false)
    private String userPhone;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReminderStatus status;
    
    @Column(nullable = false)
    private LocalDateTime scheduledAt;
    
    private LocalDateTime sentAt;
    
    private LocalDateTime createdAt;
    
    @Column(length = 2000)
    private String message;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;
    
    private Integer retryCount;
    
    @Column(length = 1000)
    private String errorMessage;
    
    @Column(length = 50)
    private String timezone; // User's timezone (e.g., "Europe/Istanbul", "America/New_York")
    
    @Column(length = 50)
    private String abTestVariant; // A/B test variant (A, B, C, etc.)
    
    @Column(nullable = false)
    private Boolean isPersonalized; // Whether message contains personalized content
    
    @Column(length = 100)
    private String recipientName; // For personalized messages
    
    @Column(length = 200)
    private String treatmentType; // Treatment type for personalized messages
    
    @Column(length = 10)
    private String language; // User's preferred language (tr, en, ar, de)
    
    public enum ReminderType {
        QUOTE_PENDING,          // Teklif bekliyor
        QUOTE_EXPIRING,         // Teklif süresi doluyor
        LEAD_FOLLOW_UP,         // Lead takibi
        PAYMENT_REMINDER,       // Ödeme hatırlatması
        APPOINTMENT_REMINDER,   // Randevu hatırlatması
        CONSULTATION_REMINDER   // Konsültasyon hatırlatması
    }
    
    public enum ReminderStatus {
        PENDING,        // Beklemede
        SENT,           // Gönderildi
        FAILED,         // Başarısız
        CANCELLED       // İptal edildi
    }
    
    public enum NotificationChannel {
        EMAIL,
        SMS,
        PUSH,
        ALL             // Tüm kanallar
    }
}
