package com.healthtourism.testimonialservice.repository;
import com.healthtourism.testimonialservice.entity.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {
    List<Testimonial> findByIsApprovedTrue();
    List<Testimonial> findByHospitalIdAndIsApprovedTrue(Long hospitalId);
    List<Testimonial> findByDoctorIdAndIsApprovedTrue(Long doctorId);
    @Query("SELECT t FROM Testimonial t WHERE t.isApproved = true ORDER BY t.createdAt DESC")
    List<Testimonial> findAllApprovedOrderByCreatedAtDesc();
}

