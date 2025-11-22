package com.healthtourism.testimonialservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "testimonials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Testimonial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private String patientName;
    @Column(nullable = false) private String country;
    @Column(nullable = false) private Long hospitalId;
    @Column private Long doctorId;
    @Column(nullable = false, length = 2000) private String testimonial;
    @Column(nullable = false) private Integer rating;
    @Column(length = 1000) private String videoUrl;
    @Column(length = 1000) private String imageUrl;
    @Column(nullable = false) private Boolean isApproved;
    @Column(nullable = false) private LocalDateTime createdAt;
}

