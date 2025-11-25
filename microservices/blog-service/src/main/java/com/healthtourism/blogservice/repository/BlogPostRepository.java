package com.healthtourism.blogservice.repository;
import com.healthtourism.blogservice.entity.BlogPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BlogPostRepository extends MongoRepository<BlogPost, String> {
    List<BlogPost> findByIsPublishedTrue();
    List<BlogPost> findByCategoryAndIsPublishedTrue(String category);
    List<BlogPost> findByIsPublishedTrueOrderByPublishedAtDesc();
    List<BlogPost> findByIsPublishedTrueOrderByViewCountDesc();
}

