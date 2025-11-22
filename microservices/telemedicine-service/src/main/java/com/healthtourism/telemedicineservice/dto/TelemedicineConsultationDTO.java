package com.healthtourism.telemedicineservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelemedicineConsultationDTO {
    private Long id;
    private Long userId;
    private Long doctorId;
    private LocalDateTime consultationDate;
    private Integer durationMinutes;
    private String consultationType;
    private String status;
    private BigDecimal fee;
    private String notes;
    private String meetingLink;
    private LocalDateTime createdAt;
}

