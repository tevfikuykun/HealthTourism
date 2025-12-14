package com.healthtourism.reviewservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String entityType; // HOSPITAL, DOCTOR, PACKAGE
    
    @Column(nullable = false)
    private Long entityId;
    
    @Column(nullable = false)
    private Double rating; // 1-5
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @ElementCollection
    @CollectionTable(name = "review_categories", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "category_rating")
    private List<CategoryRating> categoryRatings; // Hizmet, Temizlik, İletişim, vb.
    
    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url")
    private List<String> images; // Fotoğraflı yorumlar
    
    @Column(nullable = false)
    private Boolean isVerified; // Gerçek hasta doğrulama
    
    @Column(nullable = false)
    private Integer helpfulCount;
    
    @Column(nullable = false)
    private Integer notHelpfulCount;
    
    @Column
    private String doctorResponse; // Doktor/hastane yanıtı
    
    @Column
    private LocalDateTime doctorResponseDate;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private Boolean isPublished;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isVerified == null) isVerified = false;
        if (helpfulCount == null) helpfulCount = 0;
        if (notHelpfulCount == null) notHelpfulCount = 0;
        if (isPublished == null) isPublished = true;
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryRating {
        private String category; // SERVICE, CLEANLINESS, COMMUNICATION, VALUE
        private Double rating; // 1-5
    }
}

