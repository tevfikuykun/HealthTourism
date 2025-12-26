package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Enterprise-grade ReservationRepository with JOIN FETCH optimization,
 * status-based filtering, pagination support, operational reporting,
 * and double booking protection using Pessimistic Locking.
 * 
 * Özellikler:
 * - JOIN FETCH ile N+1 problemi çözümü
 * - Statü bazlı filtreleme (operasyonel yük azaltma)
 * - Pagination desteği (büyük veri setleri için)
 * - Operasyonel raporlama sorguları (günlük takvim, bugün gelecek hastalar)
 * - Double booking koruması (Pessimistic Locking ile race condition önleme)
 * - İstatistik ve raporlama sorguları
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // ==================== Performans Odaklı Sorgular (N+1 Problemini Engeller) ====================
    
    /**
     * Kullanıcının randevularını tüm detaylarıyla tek sorguda getir (JOIN FETCH ile).
     * Hastane, doktor ve konaklama bilgileri tek sorguda yüklenir.
     */
    @Query("SELECT r FROM Reservation r " +
           "JOIN FETCH r.doctor " +
           "JOIN FETCH r.hospital " +
           "LEFT JOIN FETCH r.accommodation " +
           "WHERE r.user.id = :userId " +
           "ORDER BY r.appointmentDate DESC")
    List<Reservation> findAllByUserIdWithDetails(@Param("userId") Long userId);
    
    /**
     * Kullanıcının randevularını detaylarıyla getir (pagination ile).
     */
    @Query(value = "SELECT r FROM Reservation r " +
           "JOIN FETCH r.doctor " +
           "JOIN FETCH r.hospital " +
           "LEFT JOIN FETCH r.accommodation " +
           "WHERE r.user.id = :userId",
           countQuery = "SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId")
    Page<Reservation> findAllByUserIdWithDetails(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Rezervasyon numarasından hızlı doğrulama (JOIN FETCH ile kullanıcı bilgisi).
     * Güvenlik için kritik.
     */
    @Query("SELECT r FROM Reservation r " +
           "JOIN FETCH r.user " +
           "JOIN FETCH r.doctor " +
           "JOIN FETCH r.hospital " +
           "WHERE r.reservationNumber = :resNum")
    Optional<Reservation> findByReservationNumberWithUser(@Param("resNum") String reservationNumber);
    
    /**
     * ID ile detaylı rezervasyon bilgisi (tüm ilişkilerle).
     */
    @Query("SELECT r FROM Reservation r " +
           "JOIN FETCH r.user " +
           "JOIN FETCH r.doctor " +
           "JOIN FETCH r.hospital " +
           "LEFT JOIN FETCH r.accommodation " +
           "WHERE r.id = :id")
    Optional<Reservation> findByIdWithDetails(@Param("id") Long id);
    
    // ==================== Statü Bazlı Filtreleme ====================
    
    /**
     * Kullanıcının belirli statüdeki rezervasyonları (pagination ile).
     * Operasyonel yükü azaltır - sadece gerekli statüleri getirir.
     */
    Page<Reservation> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    
    /**
     * Hastanenin belirli statüdeki rezervasyonları.
     */
    Page<Reservation> findByHospitalIdAndStatus(Long hospitalId, String status, Pageable pageable);
    
    /**
     * Doktorun belirli statüdeki rezervasyonları.
     */
    Page<Reservation> findByDoctorIdAndStatus(Long doctorId, String status, Pageable pageable);
    
    /**
     * Kullanıcının aktif rezervasyonları (CONFIRMED durumunda).
     */
    @Query("SELECT r FROM Reservation r " +
           "JOIN FETCH r.doctor " +
           "JOIN FETCH r.hospital " +
           "WHERE r.user.id = :userId " +
           "AND r.status = 'CONFIRMED' " +
           "ORDER BY r.appointmentDate ASC")
    List<Reservation> findActiveReservationsByUserId(@Param("userId") Long userId);
    
    /**
     * Bekleyen rezervasyonlar (PENDING durumunda).
     */
    @Query("SELECT r FROM Reservation r " +
           "JOIN FETCH r.user " +
           "JOIN FETCH r.doctor " +
           "JOIN FETCH r.hospital " +
           "WHERE r.status = 'PENDING' " +
           "ORDER BY r.createdAt ASC")
    Page<Reservation> findPendingReservations(Pageable pageable);
    
    // ==================== Operasyonel Raporlama ====================
    
    /**
     * Belirli bir tarihteki onaylı randevular (Admin/Hastane Paneli için).
     * Günlük takvim görünümü için kullanılır.
     * 
     * @param hospitalId Hastane ID
     * @param start Gün başlangıç zamanı
     * @param end Gün bitiş zamanı
     * @return Belirtilen tarihteki CONFIRMED randevular
     */
    @Query("SELECT r FROM Reservation r " +
           "JOIN FETCH r.user " +
           "JOIN FETCH r.doctor " +
           "WHERE r.hospital.id = :hospitalId " +
           "AND r.appointmentDate >= :start AND r.appointmentDate <= :end " +
           "AND r.status = 'CONFIRMED' " +
           "ORDER BY r.appointmentDate ASC")
    List<Reservation> findDailySchedule(
            @Param("hospitalId") Long hospitalId, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
    
    /**
     * Bugün gelecek hastalar (CONFIRMED rezervasyonlar).
     * Operasyon ekibi için kritik.
     */
    @Query("SELECT r FROM Reservation r " +
           "JOIN FETCH r.user " +
           "JOIN FETCH r.doctor " +
           "JOIN FETCH r.hospital " +
           "WHERE DATE(r.appointmentDate) = CURRENT_DATE " +
           "AND r.status = 'CONFIRMED' " +
           "ORDER BY r.appointmentDate ASC")
    List<Reservation> findTodayAppointments();
    
    /**
     * Hastane için bugün gelecek hastalar.
     */
    @Query("SELECT r FROM Reservation r " +
           "JOIN FETCH r.user " +
           "JOIN FETCH r.doctor " +
           "WHERE r.hospital.id = :hospitalId " +
           "AND DATE(r.appointmentDate) = CURRENT_DATE " +
           "AND r.status = 'CONFIRMED' " +
           "ORDER BY r.appointmentDate ASC")
    List<Reservation> findTodayAppointmentsByHospital(@Param("hospitalId") Long hospitalId);
    
    /**
     * Doktor için bugün gelecek hastalar.
     */
    @Query("SELECT r FROM Reservation r " +
           "JOIN FETCH r.user " +
           "JOIN FETCH r.hospital " +
           "WHERE r.doctor.id = :doctorId " +
           "AND DATE(r.appointmentDate) = CURRENT_DATE " +
           "AND r.status = 'CONFIRMED' " +
           "ORDER BY r.appointmentDate ASC")
    List<Reservation> findTodayAppointmentsByDoctor(@Param("doctorId") Long doctorId);
    
    /**
     * Yaklaşan randevular (önümüzdeki N gün içinde).
     */
    @Query("SELECT r FROM Reservation r " +
           "JOIN FETCH r.user " +
           "JOIN FETCH r.doctor " +
           "WHERE r.hospital.id = :hospitalId " +
           "AND r.appointmentDate BETWEEN CURRENT_TIMESTAMP AND :endDate " +
           "AND r.status = 'CONFIRMED' " +
           "ORDER BY r.appointmentDate ASC")
    List<Reservation> findUpcomingAppointments(
            @Param("hospitalId") Long hospitalId,
            @Param("endDate") LocalDateTime endDate);
    
    // ==================== Double Booking Koruması (Pessimistic Locking) ====================
    
    /**
     * Çakışan rezervasyonları bulur (Double booking kontrolü).
     * Doktorun takviminde çakışmaları önlemek sağlık turizminin temel kuralıdır.
     * 
     * Randevu çakışma mantığı:
     * - Başlangıç zamanı: appointmentDate
     * - Bitiş zamanı: appointmentDate + muayene süresi (örnek: 30 dakika)
     * İki randevu çakışır if:
     * - (r1.appointmentDate <= r2.appointmentDate + duration) AND 
     *   (r1.appointmentDate + duration >= r2.appointmentDate)
     */
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.doctor.id = :doctorId " +
           "AND r.appointmentDate BETWEEN :startDate AND :endDate " +
           "AND r.status IN ('PENDING', 'CONFIRMED') " +
           "ORDER BY r.appointmentDate ASC")
    List<Reservation> findConflictingReservations(
            @Param("doctorId") Long doctorId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Çakışma kontrolü için PESSIMISTIC LOCK kullanır (Race Condition önleme).
     * SELECT ... FOR UPDATE ile o zaman dilimi işlem bitene kadar kilitlenir.
     * 
     * CRİTİK: Bu metot @Transactional içinde çağrılmalıdır.
     * İki kişinin aynı anda aynı saniyeye randevu almasını engeller.
     * 
     * @param doctorId Doktor ID
     * @param startDate Randevu başlangıç zamanı
     * @param endDate Randevu bitiş zamanı
     * @return Çakışan rezervasyonlar (kilitli olarak getirilir)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.doctor.id = :doctorId " +
           "AND r.appointmentDate BETWEEN :startDate AND :endDate " +
           "AND r.status IN ('PENDING', 'CONFIRMED') " +
           "ORDER BY r.appointmentDate ASC")
    List<Reservation> findConflictingReservationsWithLock(
            @Param("doctorId") Long doctorId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Belirli bir randevu zamanı için çakışma kontrolü (Pessimistic Lock ile).
     * Daha spesifik kontrol için kullanılır.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.doctor.id = :doctorId " +
           "AND r.appointmentDate = :appointmentDate " +
           "AND r.status IN ('PENDING', 'CONFIRMED')")
    Boolean hasConflictingReservationAtTime(
            @Param("doctorId") Long doctorId,
            @Param("appointmentDate") LocalDateTime appointmentDate);
    
    // ==================== İstatistik ve Raporlama ====================
    
    /**
     * Hastanenin toplam randevu sayısı (statü bazlı).
     * Raporlama için kullanılır.
     */
    long countByHospitalIdAndStatus(Long hospitalId, String status);
    
    /**
     * Doktorun toplam randevu sayısı (statü bazlı).
     */
    long countByDoctorIdAndStatus(Long doctorId, String status);
    
    /**
     * Kullanıcının toplam randevu sayısı (statü bazlı).
     */
    long countByUserIdAndStatus(Long userId, String status);
    
    /**
     * Review Verification: Checks if user has a completed reservation for a doctor.
     * Security requirement: Only users with completed appointments can review.
     * 
     * @param userId User ID
     * @param doctorId Doctor ID
     * @return true if user has completed reservation with this doctor
     */
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.user.id = :userId " +
           "AND r.doctor.id = :doctorId " +
           "AND r.status = 'COMPLETED'")
    boolean existsByUserIdAndDoctorIdAndStatusCompleted(
            @Param("userId") Long userId, 
            @Param("doctorId") Long doctorId);
    
    /**
     * Review Verification: Checks if user has a completed reservation for a hospital.
     * 
     * @param userId User ID
     * @param hospitalId Hospital ID
     * @return true if user has completed reservation at this hospital
     */
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.user.id = :userId " +
           "AND r.hospital.id = :hospitalId " +
           "AND r.status = 'COMPLETED'")
    boolean existsByUserIdAndHospitalIdAndStatusCompleted(
            @Param("userId") Long userId, 
            @Param("hospitalId") Long hospitalId);
    
    /**
     * Hastane için toplam ciro (CONFIRMED rezervasyonlar).
     */
    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r " +
           "WHERE r.hospital.id = :hospitalId " +
           "AND r.status = 'CONFIRMED'")
    java.math.BigDecimal calculateTotalRevenueByHospital(@Param("hospitalId") Long hospitalId);
    
    /**
     * Belirli tarih aralığında toplam ciro.
     */
    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r " +
           "WHERE r.hospital.id = :hospitalId " +
           "AND r.status = 'CONFIRMED' " +
           "AND r.appointmentDate BETWEEN :start AND :end")
    java.math.BigDecimal calculateRevenueByHospitalForPeriod(
            @Param("hospitalId") Long hospitalId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    /**
     * Rezervasyon istatistikleri (statü bazlı sayım).
     */
    @Query("SELECT r.status, COUNT(r) FROM Reservation r " +
           "WHERE r.hospital.id = :hospitalId " +
           "GROUP BY r.status")
    List<Object[]> getReservationStatisticsByHospital(@Param("hospitalId") Long hospitalId);
    
    // ==================== Pagination Desteği ====================
    
    /**
     * Kullanıcının tüm rezervasyonları (pagination ile).
     */
    Page<Reservation> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Hastanenin tüm rezervasyonları (pagination ile).
     */
    Page<Reservation> findByHospitalId(Long hospitalId, Pageable pageable);
    
    /**
     * Doktorun tüm rezervasyonları (pagination ile).
     */
    Page<Reservation> findByDoctorId(Long doctorId, Pageable pageable);
    
    /**
     * Tarih aralığında oluşturulmuş rezervasyonlar (pagination ile).
     */
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.createdAt BETWEEN :start AND :end " +
           "ORDER BY r.createdAt DESC")
    Page<Reservation> findByCreatedAtBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);
    
    /**
     * Randevu tarihi aralığındaki rezervasyonlar (pagination ile).
     */
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.appointmentDate BETWEEN :start AND :end " +
           "ORDER BY r.appointmentDate ASC")
    Page<Reservation> findByAppointmentDateBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    
    /**
     * @deprecated Use findByUserId(Long userId, Pageable pageable) instead.
     * This method loads all records into memory which is not scalable.
     */
    @Deprecated
    List<Reservation> findByUserId(Long userId);
    
    /**
     * @deprecated Use findByHospitalId(Long hospitalId, Pageable pageable) instead.
     */
    @Deprecated
    List<Reservation> findByHospitalId(Long hospitalId);
    
    /**
     * @deprecated Use findByDoctorId(Long doctorId, Pageable pageable) instead.
     */
    @Deprecated
    List<Reservation> findByDoctorId(Long doctorId);
    
    /**
     * Basic find by reservation number (without JOIN FETCH).
     * For better performance, use findByReservationNumberWithUser instead.
     */
    Optional<Reservation> findByReservationNumber(String reservationNumber);
    
    /**
     * @deprecated Use findAllByUserIdWithDetails or findByUserId with Pageable instead.
     */
    @Deprecated
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Reservation> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}

