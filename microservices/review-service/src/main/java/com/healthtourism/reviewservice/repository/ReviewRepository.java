package com.healthtourism.reviewservice.repository;
import com.healthtourism.reviewservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<Review> findByUserId(Long userId);
    Optional<Review> findByUserIdAndEntityTypeAndEntityId(Long userId, String entityType, Long entityId);
    List<Review> findByEntityTypeAndEntityIdAndIsPublishedTrue(String entityType, Long entityId);
    List<Review> findByIsVerifiedTrue();
}

