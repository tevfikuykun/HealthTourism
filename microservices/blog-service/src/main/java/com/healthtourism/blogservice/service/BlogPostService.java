package com.healthtourism.blogservice.service;
import com.healthtourism.blogservice.dto.BlogPostDTO;
import com.healthtourism.blogservice.entity.BlogPost;
import com.healthtourism.blogservice.repository.BlogPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogPostService {
    @Autowired
    private BlogPostRepository blogPostRepository;
    
    public List<BlogPostDTO> getAllPublishedPosts() {
        return blogPostRepository.findByIsPublishedTrueOrderByPublishedAtDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<BlogPostDTO> getPostsByCategory(String category) {
        return blogPostRepository.findByCategoryAndIsPublishedTrue(category)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public BlogPostDTO getPostById(String id) {
        BlogPost post = blogPostRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Blog yazısı bulunamadı"));
        incrementViewCount(post);
        return convertToDTO(post);
    }
    
    private void incrementViewCount(BlogPost post) {
        post.setViewCount(post.getViewCount() + 1);
        blogPostRepository.save(post);
    }
    
    public BlogPostDTO createPost(BlogPost post) {
        post.setViewCount(0);
        post.setIsPublished(false);
        post.setCreatedAt(LocalDateTime.now());
        return convertToDTO(blogPostRepository.save(post));
    }
    
    private BlogPostDTO convertToDTO(BlogPost post) {
        BlogPostDTO dto = new BlogPostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthor(post.getAuthor());
        dto.setCategory(post.getCategory());
        dto.setSummary(post.getSummary());
        dto.setImageUrl(post.getImageUrl());
        dto.setViewCount(post.getViewCount());
        dto.setIsPublished(post.getIsPublished());
        dto.setPublishedAt(post.getPublishedAt());
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }
}

