package com.healthtourism.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "leads")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false)
    private String email;
    
    private String phone;
    
    private String country;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LeadStatus status;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LeadSource source;
    
    @Column(length = 2000)
    private String notes;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime convertedAt;
    
    private Long convertedToUserId;
    
    private Long assignedToUserId; // CRM agent
    
    public enum LeadStatus {
        NEW,                    // Yeni lead
        CONTACTED,              // İletişim kuruldu
        QUALIFIED,              // Nitelendirildi
        DOCUMENT_PENDING,       // Evrak bekliyor
        QUOTE_SENT,             // Teklif gönderildi
        QUOTE_ACCEPTED,         // Teklif kabul edildi
        PAYMENT_PENDING,        // Ödeme bekliyor
        CONVERTED,              // Müşteriye dönüştü
        LOST                    // Kayıp
    }
    
    public enum LeadSource {
        WEBSITE,                // Web sitesi
        SOCIAL_MEDIA,           // Sosyal medya
        REFERRAL,               // Referans
        ADVERTISEMENT,          // Reklam
        DIRECT_CONTACT,         // Doğrudan iletişim
        OTHER
    }
}
