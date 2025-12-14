package com.healthtourism.reviewservice.service;
import com.healthtourism.reviewservice.entity.Review;
import com.healthtourism.reviewservice.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository repository;
    
    public Review createReview(Review review) {
        // Kullanıcının daha önce yorum yapıp yapmadığını kontrol et
        Optional<Review> existing = repository.findByUserIdAndEntityTypeAndEntityId(
            review.getUserId(), review.getEntityType(), review.getEntityId()
        );
        if (existing.isPresent()) {
            throw new RuntimeException("Bu varlık için zaten yorum yaptınız");
        }
        return repository.save(review);
    }
    
    public Review updateReview(Long id, Review review) {
        Review existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Review bulunamadı"));
        existing.setRating(review.getRating());
        existing.setComment(review.getComment());
        existing.setCategoryRatings(review.getCategoryRatings());
        existing.setImages(review.getImages());
        return repository.save(existing);
    }
    
    public List<Review> getReviewsByEntity(String entityType, Long entityId) {
        return repository.findByEntityTypeAndEntityIdAndIsPublishedTrue(entityType, entityId);
    }
    
    public Review addDoctorResponse(Long reviewId, String response) {
        Review review = repository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review bulunamadı"));
        review.setDoctorResponse(response);
        review.setDoctorResponseDate(java.time.LocalDateTime.now());
        return repository.save(review);
    }
    
    public Review markHelpful(Long reviewId, boolean helpful) {
        Review review = repository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review bulunamadı"));
        if (helpful) {
            review.setHelpfulCount(review.getHelpfulCount() + 1);
        } else {
            review.setNotHelpfulCount(review.getNotHelpfulCount() + 1);
        }
        return repository.save(review);
    }
    
    public Review verifyReview(Long reviewId) {
        Review review = repository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review bulunamadı"));
        review.setIsVerified(true);
        return repository.save(review);
    }
}

