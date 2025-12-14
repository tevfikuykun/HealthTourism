package com.healthtourism.forumservice.controller;
import com.healthtourism.forumservice.entity.ForumPost;
import com.healthtourism.forumservice.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forum")
@CrossOrigin(origins = "*")
public class ForumController {
    @Autowired
    private ForumService service;

    @GetMapping("/posts")
    public ResponseEntity<List<ForumPost>> getPosts(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(service.getPosts(params));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<ForumPost> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPostById(id));
    }

    @PostMapping("/posts")
    public ResponseEntity<ForumPost> createPost(@RequestBody ForumPost post) {
        return ResponseEntity.ok(service.createPost(post));
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<ForumPost> updatePost(@PathVariable Long id, @RequestBody ForumPost post) {
        return ResponseEntity.ok(service.updatePost(id, post));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        service.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<ForumPost> likePost(@PathVariable Long id) {
        return ResponseEntity.ok(service.likePost(id));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> addComment(@PathVariable Long postId, @RequestBody Map<String, Object> comment) {
        return ResponseEntity.ok(service.addComment(postId, comment));
    }
}

