package com.healthtourism.doctorservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String specialization;
    private String title;
    private String bio;
    private Integer experienceYears;
    private String languages;
    private Double rating;
    private Integer totalReviews;
    private Double consultationFee;
    private Boolean isAvailable;
    private Long hospitalId;
    private String imageUrl;
    private String thumbnailUrl;
}

