package com.healthtourism.blogservice.repository;
import com.healthtourism.blogservice.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByIsPublishedTrue();
    List<BlogPost> findByCategoryAndIsPublishedTrue(String category);
    @Query("SELECT b FROM BlogPost b WHERE b.isPublished = true ORDER BY b.publishedAt DESC")
    List<BlogPost> findAllPublishedOrderByPublishedAtDesc();
    @Query("SELECT b FROM BlogPost b WHERE b.isPublished = true ORDER BY b.viewCount DESC")
    List<BlogPost> findAllPublishedOrderByViewCountDesc();
}

