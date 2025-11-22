package com.healthtourism.telemedicineservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemedicine_consultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelemedicineConsultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private Long doctorId;
    @Column(nullable = false) private LocalDateTime consultationDate;
    @Column(nullable = false) private Integer durationMinutes;
    @Column(nullable = false) private String consultationType; // VIDEO, CHAT, PHONE
    @Column(nullable = false) private String status; // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    @Column(nullable = false) private BigDecimal fee;
    @Column(length = 2000) private String notes;
    @Column(length = 1000) private String meetingLink;
    @Column(nullable = false) private LocalDateTime createdAt;
}

