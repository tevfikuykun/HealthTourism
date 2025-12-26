package com.example.HealthTourism.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;
    private Long doctorId;
    private String doctorName;
    private Long hospitalId;
    private String hospitalName;
    private String reviewType; // DOCTOR, HOSPITAL
    
    /**
     * Moderasyon flag'i: Yorum onaylanmış mı?
     * false: Onay bekliyor (PENDING)
     * true: Onaylanmış ve yayında (APPROVED)
     */
    private Boolean isApproved;
    
    /**
     * Yorum statüsü: PENDING, APPROVED, REJECTED
     */
    private String status;
}

