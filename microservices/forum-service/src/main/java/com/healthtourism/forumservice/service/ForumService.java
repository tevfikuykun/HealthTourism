package com.healthtourism.forumservice.service;
import com.healthtourism.forumservice.entity.ForumPost;
import com.healthtourism.forumservice.repository.ForumPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ForumService {
    @Autowired
    private ForumPostRepository repository;

    public List<ForumPost> getPosts(Map<String, Object> params) {
        return repository.findAll();
    }

    public ForumPost getPostById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Post bulunamadı"));
    }

    public ForumPost createPost(ForumPost post) {
        return repository.save(post);
    }

    public ForumPost updatePost(Long id, ForumPost post) {
        post.setId(id);
        return repository.save(post);
    }

    public void deletePost(Long id) {
        repository.deleteById(id);
    }

    public ForumPost likePost(Long id) {
        ForumPost post = getPostById(id);
        post.setLikeCount(post.getLikeCount() + 1);
        return repository.save(post);
    }

    public Map<String, Object> addComment(Long postId, Map<String, Object> comment) {
        return Map.of("postId", postId, "comment", comment, "success", true);
    }
}

