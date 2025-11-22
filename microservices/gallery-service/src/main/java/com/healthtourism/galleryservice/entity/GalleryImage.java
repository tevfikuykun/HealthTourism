package com.healthtourism.galleryservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "gallery_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String imageUrl;
    @Column(nullable = false) private String imageType; // HOSPITAL, DOCTOR, FACILITY, PROCEDURE
    @Column(nullable = false) private Long relatedId; // hospitalId, doctorId, etc.
    @Column(nullable = false) private String title;
    @Column(length = 1000) private String description;
    @Column(nullable = false) private Integer displayOrder;
    @Column(nullable = false) private Boolean isActive;
    @Column(nullable = false) private LocalDateTime uploadedAt;
}

