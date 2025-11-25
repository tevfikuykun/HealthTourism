package com.healthtourism.galleryservice.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "gallery_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryImage {
    @Id
    private String id;
    
    private String imageUrl;
    
    @Indexed
    private String imageType; // HOSPITAL, DOCTOR, FACILITY, PROCEDURE
    
    @Indexed
    private Long relatedId; // hospitalId, doctorId, etc.
    
    private String title;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime uploadedAt;
}

