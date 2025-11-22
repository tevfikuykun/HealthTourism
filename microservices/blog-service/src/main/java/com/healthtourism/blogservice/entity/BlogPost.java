package com.healthtourism.blogservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "blog_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String title;
    @Column(nullable = false, length = 5000) private String content;
    @Column(nullable = false) private String author;
    @Column(nullable = false) private String category; // HEALTH, NEWS, TIPS, TREATMENT
    @Column(length = 500) private String summary;
    @Column(length = 1000) private String imageUrl;
    @Column(nullable = false) private Integer viewCount;
    @Column(nullable = false) private Boolean isPublished;
    @Column(nullable = false) private LocalDateTime publishedAt;
    @Column(nullable = false) private LocalDateTime createdAt;
}

