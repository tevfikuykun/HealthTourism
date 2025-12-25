package com.example.HealthTourism.repository.projection;

/**
 * Interface-based projection for review statistics.
 * Used for performance optimization - returns only aggregate data without loading full entities.
 * 
 * Performans odaklı istatistik sorguları için kullanılan projection interface.
 * Tek sorguda toplam yorum sayısı ve ortalama puanı getirir.
 * 
 * Best Practice: Review onaylandığında, bu projection sonuçları kullanılarak
 * Doctor veya Hospital entity'sinin rating ve totalReviews alanları güncellenmelidir.
 * Bu şekilde arama performansı artırılır (Review Loop pattern).
 * 
 * Usage:
 * <pre>
 * Optional&lt;ReviewStats&gt; stats = repository.getDoctorReviewStats(doctorId);
 * if (stats.isPresent()) {
 *     Long totalCount = stats.get().getTotalCount();
 *     Double avgRating = stats.get().getAvgRating();
 *     // Doctor entity'sini güncelle
 *     doctor.setRating(avgRating);
 *     doctor.setTotalReviews(totalCount.intValue());
 * }
 * </pre>
 */
public interface ReviewStats {
    
    /**
     * Toplam onaylanmış yorum sayısı
     */
    Long getTotalCount();
    
    /**
     * Ortalama puan (1-5 arası)
     */
    Double getAvgRating();
}

