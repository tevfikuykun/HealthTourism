package com.healthtourism.blogservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String category;
    private String summary;
    private String imageUrl;
    private Integer viewCount;
    private Boolean isPublished;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}

