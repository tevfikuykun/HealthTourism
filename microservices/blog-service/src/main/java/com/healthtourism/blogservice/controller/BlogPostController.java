package com.healthtourism.blogservice.controller;
import com.healthtourism.blogservice.dto.BlogPostDTO;
import com.healthtourism.blogservice.entity.BlogPost;
import com.healthtourism.blogservice.service.BlogPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/blog")
@CrossOrigin(origins = "*")
public class BlogPostController {
    @Autowired
    private BlogPostService blogPostService;
    
    @GetMapping
    public ResponseEntity<List<BlogPostDTO>> getAllPosts() {
        return ResponseEntity.ok(blogPostService.getAllPublishedPosts());
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<BlogPostDTO>> getPostsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(blogPostService.getPostsByCategory(category));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BlogPostDTO> getPostById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(blogPostService.getPostById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<BlogPostDTO> createPost(@RequestBody BlogPost post) {
        return ResponseEntity.ok(blogPostService.createPost(post));
    }
}

