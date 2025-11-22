package com.healthtourism.patientfollowupservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_followups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientFollowUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private Long doctorId;
    @Column(nullable = false) private Long reservationId;
    @Column(nullable = false) private LocalDateTime followUpDate;
    @Column(nullable = false) private String status; // SCHEDULED, COMPLETED, CANCELLED
    @Column(length = 2000) private String notes;
    @Column(length = 2000) private String recoveryProgress;
    @Column(nullable = false) private LocalDateTime createdAt;
    @Column private LocalDateTime completedAt;
}

