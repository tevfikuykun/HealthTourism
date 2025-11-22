package com.healthtourism.patientfollowupservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientFollowUpDTO {
    private Long id;
    private Long userId;
    private Long doctorId;
    private Long reservationId;
    private LocalDateTime followUpDate;
    private String status;
    private String notes;
    private String recoveryProgress;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}

