package com.healthtourism.testimonialservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialDTO {
    private Long id;
    private Long userId;
    private String patientName;
    private String country;
    private Long hospitalId;
    private Long doctorId;
    private String testimonial;
    private Integer rating;
    private String videoUrl;
    private String imageUrl;
    private Boolean isApproved;
    private LocalDateTime createdAt;
}

