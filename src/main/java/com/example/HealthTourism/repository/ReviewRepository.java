package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.Review;
import com.example.HealthTourism.repository.projection.ReviewStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Enterprise-grade ReviewRepository with moderation and approval mechanism,
 * performance optimization, and analytics support.
 * 
 * Özellikler:
 * - Moderasyon ve onay mekanizması (isApproved/status bazlı filtreleme)
 * - Sadece onaylanmış yorumları gösterme
 * - JOIN FETCH ile N+1 problemi çözümü
 * - Performans odaklı istatistik sorguları (Projection kullanarak)
 * - Admin paneli sorguları (onay bekleyen yorumlar)
 * - Rating güncelleme mekanizması için yardımcı metotlar
 * - Pagination desteği
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // ==================== Moderasyon ve Onay Mekanizması ====================
    
    /**
     * Sadece onaylanmış yorumları en yeniye göre getir (doktor için, pagination ile).
     * Kurumsal gereklilik: Uygunsuz yorumların profilde görünmesini engeller.
     */
    @Query("SELECT r FROM Review r WHERE r.doctor.id = :doctorId AND r.isApproved = true " +
           "ORDER BY r.createdAt DESC")
    Page<Review> findApprovedReviewsByDoctor(@Param("doctorId") Long doctorId, Pageable pageable);
    
    /**
     * Sadece onaylanmış yorumları en yeniye göre getir (hastane için, pagination ile).
     */
    @Query("SELECT r FROM Review r WHERE r.hospital.id = :hospitalId AND r.isApproved = true " +
           "ORDER BY r.createdAt DESC")
    Page<Review> findApprovedReviewsByHospital(@Param("hospitalId") Long hospitalId, Pageable pageable);
    
    /**
     * Admin Paneli: Onay bekleyen yorumları listele (en eski önce).
     */
    @Query("SELECT r FROM Review r " +
           "JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.doctor " +
           "LEFT JOIN FETCH r.hospital " +
           "WHERE r.isApproved = false " +
           "ORDER BY r.createdAt ASC")
    List<Review> findByIsApprovedFalseOrderByCreatedAtAsc();
    
    /**
     * Onay bekleyen yorumlar (pagination ile).
     */
    @Query(value = "SELECT r FROM Review r " +
           "JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.doctor " +
           "LEFT JOIN FETCH r.hospital " +
           "WHERE r.isApproved = false",
           countQuery = "SELECT COUNT(r) FROM Review r WHERE r.isApproved = false")
    Page<Review> findPendingReviews(Pageable pageable);
    
    /**
     * Statü bazlı filtreleme (PENDING, APPROVED, REJECTED).
     */
    Page<Review> findByStatus(String status, Pageable pageable);
    
    // ==================== Performans Odaklı İstatistik Sorguları (Projection) ====================
    
    /**
     * Doktor için onaylanmış yorum istatistikleri (tek sorguda adet ve ortalama).
     * Performans odaklı: Projection kullanarak sadece gerekli alanları getirir.
     * 
     * Best Practice: Bu metot onaylanmış yorumları baz alır.
     * Yorum onaylandığında Service katmanında Doctor entity'sinin
     * rating ve totalReviews alanları bu sonuçlarla güncellenmelidir.
     */
    @Query("SELECT COUNT(r) as totalCount, AVG(CAST(r.rating AS DOUBLE)) as avgRating " +
           "FROM Review r " +
           "WHERE r.doctor.id = :doctorId AND r.isApproved = true")
    Optional<ReviewStats> getDoctorReviewStats(@Param("doctorId") Long doctorId);
    
    /**
     * Hastane için onaylanmış yorum istatistikleri.
     */
    @Query("SELECT COUNT(r) as totalCount, AVG(CAST(r.rating AS DOUBLE)) as avgRating " +
           "FROM Review r " +
           "WHERE r.hospital.id = :hospitalId AND r.isApproved = true")
    Optional<ReviewStats> getHospitalReviewStats(@Param("hospitalId") Long hospitalId);
    
    // ==================== Rating Güncelleme Mekanizması ====================
    
    /**
     * Doktor için ortalama puan hesaplama (sadece onaylanmış yorumlar).
     * Review Loop: Yorum onaylandığında Doctor entity'sinin rating alanını
     * güncellemek için kullanılır.
     */
    @Query("SELECT AVG(CAST(r.rating AS DOUBLE)) FROM Review r " +
           "WHERE r.doctor.id = :doctorId AND r.isApproved = true")
    Double calculateAverageRatingByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Doktor için onaylanmış yorum sayısı.
     */
    @Query("SELECT COUNT(r) FROM Review r " +
           "WHERE r.doctor.id = :doctorId AND r.isApproved = true")
    Long countApprovedReviewsByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Hastane için ortalama puan hesaplama (sadece onaylanmış yorumlar).
     */
    @Query("SELECT AVG(CAST(r.rating AS DOUBLE)) FROM Review r " +
           "WHERE r.hospital.id = :hospitalId AND r.isApproved = true")
    Double calculateAverageRatingByHospitalId(@Param("hospitalId") Long hospitalId);
    
    /**
     * Hastane için onaylanmış yorum sayısı.
     */
    @Query("SELECT COUNT(r) FROM Review r " +
           "WHERE r.hospital.id = :hospitalId AND r.isApproved = true")
    Long countApprovedReviewsByHospitalId(@Param("hospitalId") Long hospitalId);
    
    // ==================== Performans Odaklı Sorgular (N+1 Problemini Engeller) ====================
    
    /**
     * Kullanıcının yorumlarını detaylarıyla getir (JOIN FETCH ile).
     * Doktor ve hastane bilgileri tek sorguda yüklenir.
     */
    @Query("SELECT r FROM Review r " +
           "LEFT JOIN FETCH r.doctor " +
           "LEFT JOIN FETCH r.hospital " +
           "WHERE r.user.id = :userId " +
           "ORDER BY r.createdAt DESC")
    List<Review> findAllByUserIdWithDetails(@Param("userId") Long userId);
    
    /**
     * Doktorun onaylanmış yorumlarını detaylarıyla getir (JOIN FETCH ile).
     */
    @Query("SELECT r FROM Review r " +
           "JOIN FETCH r.user " +
           "WHERE r.doctor.id = :doctorId AND r.isApproved = true " +
           "ORDER BY r.createdAt DESC")
    List<Review> findApprovedReviewsByDoctorWithDetails(@Param("doctorId") Long doctorId);
    
    /**
     * Hastanenin onaylanmış yorumlarını detaylarıyla getir (JOIN FETCH ile).
     */
    @Query("SELECT r FROM Review r " +
           "JOIN FETCH r.user " +
           "WHERE r.hospital.id = :hospitalId AND r.isApproved = true " +
           "ORDER BY r.createdAt DESC")
    List<Review> findApprovedReviewsByHospitalWithDetails(@Param("hospitalId") Long hospitalId);
    
    // ==================== Pagination Desteği ====================
    
    /**
     * Kullanıcının tüm yorumları (pagination ile).
     */
    Page<Review> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Kullanıcının onaylanmış yorumları (pagination ile).
     */
    Page<Review> findByUserIdAndIsApprovedTrue(Long userId, Pageable pageable);
    
    /**
     * Doktorun tüm yorumları (pagination ile).
     */
    Page<Review> findByDoctorId(Long doctorId, Pageable pageable);
    
    /**
     * Hastanenin tüm yorumları (pagination ile).
     */
    Page<Review> findByHospitalId(Long hospitalId, Pageable pageable);
    
    // ==================== Yorum İstatistikleri (Detaylı) ====================
    
    /**
     * Doktor için puan dağılımı (5 yıldız, 4 yıldız, vb. kaç yorum var?).
     */
    @Query("SELECT r.rating, COUNT(r) as count " +
           "FROM Review r " +
           "WHERE r.doctor.id = :doctorId AND r.isApproved = true " +
           "GROUP BY r.rating " +
           "ORDER BY r.rating DESC")
    List<Object[]> getDoctorRatingDistribution(@Param("doctorId") Long doctorId);
    
    /**
     * Hastane için puan dağılımı.
     */
    @Query("SELECT r.rating, COUNT(r) as count " +
           "FROM Review r " +
           "WHERE r.hospital.id = :hospitalId AND r.isApproved = true " +
           "GROUP BY r.rating " +
           "ORDER BY r.rating DESC")
    List<Object[]> getHospitalRatingDistribution(@Param("hospitalId") Long hospitalId);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    
    /**
     * @deprecated Use findApprovedReviewsByDoctor(Long doctorId, Pageable pageable) instead.
     * This method returns all reviews including unapproved ones.
     */
    @Deprecated
    List<Review> findByDoctorId(Long doctorId);
    
    /**
     * @deprecated Use findApprovedReviewsByDoctor(Long doctorId, Pageable pageable) instead.
     */
    @Deprecated
    @Query("SELECT r FROM Review r WHERE r.doctor.id = :doctorId ORDER BY r.createdAt DESC")
    List<Review> findByDoctorIdOrderByCreatedAtDesc(@Param("doctorId") Long doctorId);
    
    /**
     * @deprecated Use findApprovedReviewsByHospital(Long hospitalId, Pageable pageable) instead.
     */
    @Deprecated
    @Query("SELECT r FROM Review r WHERE r.hospital.id = :hospitalId ORDER BY r.createdAt DESC")
    List<Review> findByHospitalIdOrderByCreatedAtDesc(@Param("hospitalId") Long hospitalId);
    
    /**
     * @deprecated Use calculateAverageRatingByDoctorId() instead - this includes unapproved reviews.
     */
    @Deprecated
    @Query("SELECT AVG(CAST(r.rating AS DOUBLE)) FROM Review r WHERE r.doctor.id = :doctorId")
    Double calculateAverageRatingByDoctorIdLegacy(@Param("doctorId") Long doctorId);
    
    /**
     * @deprecated Use countApprovedReviewsByDoctorId() instead.
     */
    @Deprecated
    @Query("SELECT COUNT(r) FROM Review r WHERE r.doctor.id = :doctorId")
    Long countByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * @deprecated Use calculateAverageRatingByHospitalId() instead - this includes unapproved reviews.
     */
    @Deprecated
    @Query("SELECT AVG(CAST(r.rating AS DOUBLE)) FROM Review r WHERE r.hospital.id = :hospitalId")
    Double calculateAverageRatingByHospitalIdLegacy(@Param("hospitalId") Long hospitalId);
    
    /**
     * @deprecated Use countApprovedReviewsByHospitalId() instead.
     */
    @Deprecated
    @Query("SELECT COUNT(r) FROM Review r WHERE r.hospital.id = :hospitalId")
    Long countByHospitalId(@Param("hospitalId") Long hospitalId);
    
    /**
     * @deprecated Use findByUserId(Long userId, Pageable pageable) instead.
     */
    @Deprecated
    List<Review> findByUserId(Long userId);
}

