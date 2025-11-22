package com.healthtourism.galleryservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryImageDTO {
    private Long id;
    private String imageUrl;
    private String imageType;
    private Long relatedId;
    private String title;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime uploadedAt;
}

