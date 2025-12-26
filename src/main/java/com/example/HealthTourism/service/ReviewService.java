package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.ReviewDTO;
import com.example.HealthTourism.entity.Doctor;
import com.example.HealthTourism.entity.Hospital;
import com.example.HealthTourism.entity.Review;
import com.example.HealthTourism.entity.User;
import com.example.HealthTourism.exception.*;
import com.example.HealthTourism.mapper.ReviewMapper;
import com.example.HealthTourism.repository.DoctorRepository;
import com.example.HealthTourism.repository.HospitalRepository;
import com.example.HealthTourism.repository.ReservationRepository;
import com.example.HealthTourism.repository.ReviewRepository;
import com.example.HealthTourism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enterprise-grade ReviewService with:
 * - Constructor Injection for testability
 * - MapStruct for entity-DTO mapping
 * - Transactional management
 * - Security: Reservation verification (only completed appointments can review)
 * - Duplicate review prevention
 * - Moderated reviews (isApproved flag)
 * - Asynchronous rating updates (@Async)
 * - Incremental rating calculation (performance optimization)
 * - Proper exception handling
 */
@Slf4j
@Service
@RequiredArgsConstructor // Constructor Injection - Spring best practice
@Transactional(readOnly = true) // Default: read-only for all methods (performance optimization)
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewMapper reviewMapper;

    /**
     * Gets approved reviews for a doctor.
     * Business Logic: Only shows approved reviews (moderation system).
     * 
     * @param doctorId Doctor ID
     * @return List of approved reviews
     */
    public List<ReviewDTO> getReviewsByDoctor(Long doctorId) {
        log.debug("Fetching approved reviews for doctor ID: {}", doctorId);
        return reviewRepository.findApprovedReviewsByDoctorWithDetails(doctorId)
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets approved reviews for a hospital.
     * 
     * @param hospitalId Hospital ID
     * @return List of approved reviews
     */
    public List<ReviewDTO> getReviewsByHospital(Long hospitalId) {
        log.debug("Fetching approved reviews for hospital ID: {}", hospitalId);
        return reviewRepository.findApprovedReviewsByHospitalWithDetails(hospitalId)
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Creates a verified review for a doctor.
     * Business Logic:
     * - Validates user has completed reservation with this doctor (SECURITY)
     * - Prevents duplicate reviews
     * - Validates rating (1-5)
     * - Creates review with isApproved=false (moderation required)
     * - Asynchronously updates doctor rating (performance)
     * 
     * @param userId User ID
     * @param doctorId Doctor ID
     * @param rating Rating (1-5)
     * @param comment Review comment
     * @return Created ReviewDTO
     * @throws UnverifiedReviewException if user has no completed reservation
     * @throws DuplicateReviewException if user already reviewed this doctor
     * @throws InvalidRatingException if rating is not 1-5
     */
    @Transactional // Override read-only for write operation
    public ReviewDTO createReview(Long userId, Long doctorId, Integer rating, String comment) {
        log.info("Creating review for doctor ID: {} by user ID: {}", doctorId, userId);
        
        // 1. Business logic validation: Rating must be 1-5
        validateRating(rating);
        
        // 2. Validate entities exist
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı: " + doctorId));
        
        // 3. SECURITY: Verify user has completed reservation with this doctor
        boolean hasCompletedReservation = reservationRepository
                .existsByUserIdAndDoctorIdAndStatusCompleted(userId, doctorId);
        
        if (!hasCompletedReservation) {
            throw new UnverifiedReviewException(
                    String.format("Sadece tamamlanmış randevular için yorum yapabilirsiniz. " +
                            "Lütfen önce %s %s %s için bir randevu tamamlayın.", 
                            doctor.getTitle(), doctor.getFirstName(), doctor.getLastName()));
        }
        
        // 4. Business logic: Prevent duplicate reviews
        // Check if user already has a review for this doctor
        List<Review> userReviews = reviewRepository.findAllByUserIdWithDetails(userId);
        boolean alreadyReviewed = userReviews.stream()
                .anyMatch(r -> r.getDoctor() != null && r.getDoctor().getId().equals(doctorId));
        
        if (alreadyReviewed) {
            String doctorName = doctor.getTitle() + " " + doctor.getFirstName() + " " + doctor.getLastName();
            throw new DuplicateReviewException("DOKTOR", doctorName);
        }
        
        // 5. Create review (pending approval by default)
        Review review = new Review();
        review.setUser(user);
        review.setDoctor(doctor);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        review.setReviewType("DOCTOR");
        review.setIsApproved(false); // Moderasyon: Admin onayı bekliyor
        review.setStatus("PENDING");
        
        Review saved = reviewRepository.save(review);
        log.info("Review created with ID: {} (pending approval)", saved.getId());
        
        // 6. Asynchronously update doctor rating (doesn't block user response)
        // Note: Rating is only updated when review is approved (see approveReview method)
        
        return reviewMapper.toDto(saved);
    }

    /**
     * Creates a verified review for a hospital.
     * Same security and validation checks as doctor review.
     * 
     * @param userId User ID
     * @param hospitalId Hospital ID
     * @param rating Rating (1-5)
     * @param comment Review comment
     * @return Created ReviewDTO
     */
    @Transactional
    public ReviewDTO createHospitalReview(Long userId, Long hospitalId, Integer rating, String comment) {
        log.info("Creating review for hospital ID: {} by user ID: {}", hospitalId, userId);
        
        // 1. Business logic validation: Rating must be 1-5
        validateRating(rating);
        
        // 2. Validate entities exist
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hastane bulunamadı: " + hospitalId));
        
        // 3. SECURITY: Verify user has completed reservation at this hospital
        boolean hasCompletedReservation = reservationRepository
                .existsByUserIdAndHospitalIdAndStatusCompleted(userId, hospitalId);
        
        if (!hasCompletedReservation) {
            throw new UnverifiedReviewException(
                    String.format("Sadece tamamlanmış randevular için yorum yapabilirsiniz. " +
                            "Lütfen önce %s hastanesi için bir randevu tamamlayın.", hospital.getName()));
        }
        
        // 4. Business logic: Prevent duplicate reviews
        // Check if user already has a review for this hospital
        List<Review> userReviews = reviewRepository.findAllByUserIdWithDetails(userId);
        boolean alreadyReviewed = userReviews.stream()
                .anyMatch(r -> r.getHospital() != null && r.getHospital().getId().equals(hospitalId));
        
        if (alreadyReviewed) {
            throw new DuplicateReviewException("HASTANE", hospital.getName());
        }
        
        // 5. Create review (pending approval by default)
        Review review = new Review();
        review.setUser(user);
        review.setHospital(hospital);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        review.setReviewType("HOSPITAL");
        review.setIsApproved(false); // Moderasyon: Admin onayı bekliyor
        review.setStatus("PENDING");
        
        Review saved = reviewRepository.save(review);
        log.info("Hospital review created with ID: {} (pending approval)", saved.getId());
        
        return reviewMapper.toDto(saved);
    }
    
    /**
     * Admin function: Approves a review and updates ratings.
     * Business Logic:
     * - Marks review as approved
     * - Updates doctor/hospital rating asynchronously using incremental calculation
     * 
     * @param reviewId Review ID to approve
     * @return Updated ReviewDTO
     */
    @Transactional
    public ReviewDTO approveReview(Long reviewId) {
        log.info("Approving review ID: {}", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Yorum bulunamadı: " + reviewId));
        
        review.setIsApproved(true);
        review.setStatus("APPROVED");
        Review saved = reviewRepository.save(review);
        
        // Asynchronously update ratings (performance optimization)
        if ("DOCTOR".equals(review.getReviewType()) && review.getDoctor() != null) {
            updateDoctorRatingIncremental(review.getDoctor().getId(), review.getRating());
        } else if ("HOSPITAL".equals(review.getReviewType()) && review.getHospital() != null) {
            updateHospitalRatingIncremental(review.getHospital().getId(), review.getRating());
        }
        
        log.info("Review ID: {} approved and ratings updated", reviewId);
        return reviewMapper.toDto(saved);
    }
    
    /**
     * Admin function: Rejects a review.
     * 
     * @param reviewId Review ID to reject
     */
    @Transactional
    public void rejectReview(Long reviewId) {
        log.info("Rejecting review ID: {}", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Yorum bulunamadı: " + reviewId));
        
        review.setIsApproved(false);
        review.setStatus("REJECTED");
        reviewRepository.save(review);
        
        log.info("Review ID: {} rejected", reviewId);
    }
    
    /**
     * Gets pending reviews for admin moderation.
     * 
     * @return List of pending reviews
     */
    public List<ReviewDTO> getPendingReviews() {
        log.debug("Fetching pending reviews for moderation");
        return reviewRepository.findByIsApprovedFalseOrderByCreatedAtAsc()
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }
    
    // ==================== Private Helper Methods ====================
    
    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new InvalidRatingException(rating);
        }
    }
    
    /**
     * Updates doctor rating using INCREMENTAL calculation (performance optimization).
     * Formula: NewRating = ((OldAvg × OldCount) + NewRating) / (OldCount + 1)
     * 
     * This avoids recalculating all reviews from database, reducing load significantly.
     * 
     * @param doctorId Doctor ID
     * @param newRating New rating to add
     */
    @Async // Asynchronous execution - doesn't block main thread
    @Transactional // Separate transaction for rating update
    public void updateDoctorRatingIncremental(Long doctorId, Integer newRating) {
        log.debug("Asynchronously updating rating for doctor ID: {} with new rating: {}", doctorId, newRating);
        
        try {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("Doktor bulunamadı: " + doctorId));
            
            Double oldRating = doctor.getRating();
            Integer oldCount = doctor.getTotalReviews();
            
            // Incremental calculation
            // Formula: NewAvg = ((OldAvg × OldCount) + NewRating) / (OldCount + 1)
            if (oldCount == 0) {
                // First review
                doctor.setRating(newRating.doubleValue());
                doctor.setTotalReviews(1);
            } else {
                BigDecimal newRatingBigDecimal = BigDecimal.valueOf(newRating);
                BigDecimal oldRatingBigDecimal = BigDecimal.valueOf(oldRating);
                BigDecimal oldCountBigDecimal = BigDecimal.valueOf(oldCount);
                
                // Calculate: (oldRating * oldCount + newRating) / (oldCount + 1)
                BigDecimal numerator = oldRatingBigDecimal.multiply(oldCountBigDecimal)
                        .add(newRatingBigDecimal);
                BigDecimal denominator = oldCountBigDecimal.add(BigDecimal.ONE);
                
                BigDecimal newAverage = numerator.divide(denominator, 2, RoundingMode.HALF_UP);
                
                doctor.setRating(newAverage.doubleValue());
                doctor.setTotalReviews(oldCount + 1);
            }
            
            doctorRepository.save(doctor);
            log.debug("Successfully updated rating for doctor ID: {} to: {}", doctorId, doctor.getRating());
            
        } catch (Exception e) {
            log.error("Error updating doctor rating for ID: {}", doctorId, e);
            // Note: In production, you might want to retry or send to a dead letter queue
        }
    }
    
    /**
     * Updates hospital rating using INCREMENTAL calculation (performance optimization).
     * Same formula as doctor rating update.
     * 
     * @param hospitalId Hospital ID
     * @param newRating New rating to add
     */
    @Async // Asynchronous execution
    @Transactional
    public void updateHospitalRatingIncremental(Long hospitalId, Integer newRating) {
        log.debug("Asynchronously updating rating for hospital ID: {} with new rating: {}", hospitalId, newRating);
        
        try {
            Hospital hospital = hospitalRepository.findById(hospitalId)
                    .orElseThrow(() -> new RuntimeException("Hastane bulunamadı: " + hospitalId));
            
            Double oldRating = hospital.getRating();
            Integer oldCount = hospital.getTotalReviews();
            
            // Incremental calculation
            if (oldCount == 0) {
                hospital.setRating(newRating.doubleValue());
                hospital.setTotalReviews(1);
            } else {
                BigDecimal newRatingBigDecimal = BigDecimal.valueOf(newRating);
                BigDecimal oldRatingBigDecimal = BigDecimal.valueOf(oldRating);
                BigDecimal oldCountBigDecimal = BigDecimal.valueOf(oldCount);
                
                BigDecimal numerator = oldRatingBigDecimal.multiply(oldCountBigDecimal)
                        .add(newRatingBigDecimal);
                BigDecimal denominator = oldCountBigDecimal.add(BigDecimal.ONE);
                
                BigDecimal newAverage = numerator.divide(denominator, 2, RoundingMode.HALF_UP);
                
                hospital.setRating(newAverage.doubleValue());
                hospital.setTotalReviews(oldCount + 1);
            }
            
            hospitalRepository.save(hospital);
            log.debug("Successfully updated rating for hospital ID: {} to: {}", hospitalId, hospital.getRating());
            
        } catch (Exception e) {
            log.error("Error updating hospital rating for ID: {}", hospitalId, e);
        }
    }
    
    /**
     * Legacy method: Updates doctor rating by recalculating from all reviews.
     * @deprecated Use updateDoctorRatingIncremental for better performance.
     * This method recalculates from database, which is slower for large datasets.
     */
    @Deprecated
    @Transactional
    private void updateDoctorRating(Long doctorId) {
        Double averageRating = reviewRepository.calculateAverageRatingByDoctorId(doctorId);
        Long totalReviews = reviewRepository.countApprovedReviewsByDoctorId(doctorId);
        
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı: " + doctorId));
        
        doctor.setRating(averageRating != null ? averageRating : 0.0);
        doctor.setTotalReviews(totalReviews != null ? totalReviews.intValue() : 0);
        
        doctorRepository.save(doctor);
    }

    /**
     * Legacy method: Updates hospital rating by recalculating from all reviews.
     * @deprecated Use updateHospitalRatingIncremental for better performance.
     */
    @Deprecated
    @Transactional
    private void updateHospitalRating(Long hospitalId) {
        Double averageRating = reviewRepository.calculateAverageRatingByHospitalId(hospitalId);
        Long totalReviews = reviewRepository.countApprovedReviewsByHospitalId(hospitalId);
        
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hastane bulunamadı: " + hospitalId));
        
        hospital.setRating(averageRating != null ? averageRating : 0.0);
        hospital.setTotalReviews(totalReviews != null ? totalReviews.intValue() : 0);
        
        hospitalRepository.save(hospital);
    }
}
