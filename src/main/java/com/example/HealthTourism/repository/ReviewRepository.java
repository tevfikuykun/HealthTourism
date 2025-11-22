package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByDoctorId(Long doctorId);
    
    List<Review> findByUserId(Long userId);
    
    @Query("SELECT r FROM Review r WHERE r.doctor.id = :doctorId ORDER BY r.createdAt DESC")
    List<Review> findByDoctorIdOrderByCreatedAtDesc(@Param("doctorId") Long doctorId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.doctor.id = :doctorId")
    Double calculateAverageRatingByDoctorId(@Param("doctorId") Long doctorId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.doctor.id = :doctorId")
    Long countByDoctorId(@Param("doctorId") Long doctorId);
}

