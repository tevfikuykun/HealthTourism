package com.healthtourism.blogservice.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;

import java.time.LocalDateTime;

@Document(collection = "blog_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost {
    @Id
    private String id;
    
    @TextIndexed
    private String title;
    
    @TextIndexed
    private String content;
    
    private String author;
    
    @Indexed
    private String category; // HEALTH, NEWS, TIPS, TREATMENT
    
    private String summary;
    private String imageUrl;
    private Integer viewCount;
    
    @Indexed
    private Boolean isPublished;
    
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}

