package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.ReviewDTO;
import com.example.HealthTourism.entity.Doctor;
import com.example.HealthTourism.entity.Review;
import com.example.HealthTourism.entity.User;
import com.example.HealthTourism.repository.DoctorRepository;
import com.example.HealthTourism.repository.ReviewRepository;
import com.example.HealthTourism.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<ReviewDTO> getReviewsByDoctor(Long doctorId) {
        return reviewRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ReviewDTO createReview(Long userId, Long doctorId, Integer rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Puan 1-5 arasında olmalıdır");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));
        
        Review review = new Review();
        review.setUser(user);
        review.setDoctor(doctor);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        
        Review saved = reviewRepository.save(review);
        
        // Doktorun ortalama puanını güncelle
        updateDoctorRating(doctorId);
        
        return convertToDTO(saved);
    }
    
    @Transactional
    private void updateDoctorRating(Long doctorId) {
        Double averageRating = reviewRepository.calculateAverageRatingByDoctorId(doctorId);
        Long totalReviews = reviewRepository.countByDoctorId(doctorId);
        
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));
        
        doctor.setRating(averageRating != null ? averageRating : 0.0);
        doctor.setTotalReviews(totalReviews != null ? totalReviews.intValue() : 0);
        
        doctorRepository.save(doctor);
    }
    
    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getFirstName() + " " + review.getUser().getLastName());
        dto.setDoctorId(review.getDoctor().getId());
        dto.setDoctorName(review.getDoctor().getTitle() + " " + 
                         review.getDoctor().getFirstName() + " " + 
                         review.getDoctor().getLastName());
        return dto;
    }
}

