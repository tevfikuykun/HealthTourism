package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.TransferReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Enterprise-grade TransferReservationRepository with operational tracking,
 * performance optimization, and flight number tracking support.
 * 
 * Özellikler:
 * - JOIN FETCH ile N+1 problemi çözümü
 * - Operasyonel takip sorguları (günlük takvim, bugün yapılacak transferler)
 * - Statü bazlı filtreleme (operasyonel yük azaltma)
 * - Uçuş numarası takibi (flight tracking bots için)
 * - Pagination desteği
 * - Dashboard istatistikleri
 */
@Repository
public interface TransferReservationRepository extends JpaRepository<TransferReservation, Long> {
    
    // ==================== Performans Odaklı Sorgular (N+1 Problemini Engeller) ====================
    
    /**
     * Kullanıcının transferlerini servis detaylarıyla getir (JOIN FETCH ile).
     * TransferService bilgisi tek sorguda yüklenir.
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "JOIN FETCH tr.transferService " +
           "WHERE tr.user.id = :userId " +
           "ORDER BY tr.transferDateTime DESC")
    List<TransferReservation> findAllByUserIdWithDetails(@Param("userId") Long userId);
    
    /**
     * Kullanıcının transferlerini detaylarıyla getir (pagination ile).
     */
    @Query(value = "SELECT tr FROM TransferReservation tr " +
           "JOIN FETCH tr.transferService " +
           "JOIN FETCH tr.user " +
           "WHERE tr.user.id = :userId",
           countQuery = "SELECT COUNT(tr) FROM TransferReservation tr WHERE tr.user.id = :userId")
    Page<TransferReservation> findAllByUserIdWithDetails(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Rezervasyon numarası ile hızlı doğrulama (tüm detaylarla).
     * Güvenlik için kritik.
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "JOIN FETCH tr.user " +
           "JOIN FETCH tr.transferService " +
           "WHERE tr.reservationNumber = :resNum")
    Optional<TransferReservation> findByReservationNumberWithDetails(@Param("resNum") String reservationNumber);
    
    /**
     * ID ile detaylı transfer rezervasyon bilgisi (tüm ilişkilerle).
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "JOIN FETCH tr.user " +
           "JOIN FETCH tr.transferService " +
           "WHERE tr.id = :id")
    Optional<TransferReservation> findByIdWithDetails(@Param("id") Long id);
    
    // ==================== Operasyonel Takip Sorguları ====================
    
    /**
     * Belirli bir gündeki tüm aktif transferler (Şoför paneli için).
     * Transferler tam zamanında gerçekleşmelidir - operasyon ekibinin
     * günlük iş listesini oluşturması için şarttır.
     * 
     * @param start Gün başlangıç zamanı
     * @param end Gün bitiş zamanı
     * @return CONFIRMED statüsündeki transferler (tarih sırasına göre)
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "JOIN FETCH tr.user " +
           "JOIN FETCH tr.transferService " +
           "WHERE tr.transferDateTime BETWEEN :start AND :end " +
           "AND tr.status = 'CONFIRMED' " +
           "ORDER BY tr.transferDateTime ASC")
    List<TransferReservation> findDailySchedule(
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
    
    /**
     * Bugün yapılacak transferler.
     * Operasyon ekibi (şoförler) için günlük iş listesi.
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "JOIN FETCH tr.user " +
           "JOIN FETCH tr.transferService " +
           "WHERE DATE(tr.transferDateTime) = CURRENT_DATE " +
           "AND tr.status = 'CONFIRMED' " +
           "ORDER BY tr.transferDateTime ASC")
    List<TransferReservation> findTodayTransfers();
    
    /**
     * Belirli transfer servisi için bugün yapılacak transferler.
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "JOIN FETCH tr.user " +
           "WHERE tr.transferService.id = :serviceId " +
           "AND DATE(tr.transferDateTime) = CURRENT_DATE " +
           "AND tr.status = 'CONFIRMED' " +
           "ORDER BY tr.transferDateTime ASC")
    List<TransferReservation> findTodayTransfersByService(@Param("serviceId") Long serviceId);
    
    /**
     * Yaklaşan transferler (önümüzdeki N saat içinde).
     * Operasyon ekibi için planlama amaçlı.
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "JOIN FETCH tr.user " +
           "JOIN FETCH tr.transferService " +
           "WHERE tr.transferDateTime BETWEEN CURRENT_TIMESTAMP AND :endTime " +
           "AND tr.status = 'CONFIRMED' " +
           "ORDER BY tr.transferDateTime ASC")
    List<TransferReservation> findUpcomingTransfers(@Param("endTime") LocalDateTime endTime);
    
    // ==================== Statü Bazlı Filtreleme ====================
    
    /**
     * Kullanıcının belirli statüdeki transferleri (pagination ile).
     * Sadece "Bekleyen" (PENDING) veya "Onaylanmış" (CONFIRMED) transferleri görmek,
     * iptal edilmiş olanların iş listesini kirletmesini önler.
     */
    Page<TransferReservation> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    
    /**
     * Belirli statüdeki tüm transferler (pagination ile).
     */
    Page<TransferReservation> findByStatus(String status, Pageable pageable);
    
    /**
     * Bekleyen transferler (PENDING durumunda).
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "JOIN FETCH tr.user " +
           "JOIN FETCH tr.transferService " +
           "WHERE tr.status = 'PENDING' " +
           "ORDER BY tr.transferDateTime ASC")
    List<TransferReservation> findPendingTransfers();
    
    /**
     * Aktif transferler (CONFIRMED durumunda, pagination ile).
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "WHERE tr.status = 'CONFIRMED' " +
           "ORDER BY tr.transferDateTime ASC")
    Page<TransferReservation> findActiveTransfers(Pageable pageable);
    
    // ==================== Uçuş Numarası Takibi (Flight Tracking) ====================
    
    /**
     * Uçuş numarasına göre transfer rezervasyonu bulma.
     * Sağlık turizminde havalimanı karşılaması için kritik.
     * Uçuş takip servisleri (flight tracking bots) rötarları kontrol ederken
     * bu metodu kullanarak ilgili transfer rezervasyonunu bulur.
     * 
     * @param flightNumber Uçuş numarası (örn: "TK1234", "LH5678")
     * @param status Rezervasyon statüsü (genellikle "CONFIRMED")
     * @return İlgili transfer rezervasyonları
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "JOIN FETCH tr.user " +
           "JOIN FETCH tr.transferService " +
           "WHERE tr.flightNumber = :flightNumber " +
           "AND tr.status = :status " +
           "ORDER BY tr.transferDateTime ASC")
    List<TransferReservation> findByFlightNumberAndStatus(
            @Param("flightNumber") String flightNumber,
            @Param("status") String status);
    
    /**
     * Uçuş numarasına göre arama (statü filtresi olmadan).
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "JOIN FETCH tr.user " +
           "JOIN FETCH tr.transferService " +
           "WHERE tr.flightNumber = :flightNumber " +
           "ORDER BY tr.transferDateTime ASC")
    List<TransferReservation> findByFlightNumber(@Param("flightNumber") String flightNumber);
    
    /**
     * Special requests içinde uçuş numarası arama (fallback).
     * Bazı durumlarda uçuş numarası specialRequests alanında saklanmış olabilir.
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "JOIN FETCH tr.user " +
           "JOIN FETCH tr.transferService " +
           "WHERE tr.specialRequests LIKE CONCAT('%', :flightNumber, '%') " +
           "AND tr.status = :status " +
           "ORDER BY tr.transferDateTime ASC")
    List<TransferReservation> findBySpecialRequestsContainingAndStatus(
            @Param("flightNumber") String flightNumber,
            @Param("status") String status);
    
    // ==================== Dashboard İstatistikleri ====================
    
    /**
     * Belirli bir statüdeki transferlerin sayısı (Dashboard için).
     */
    long countByStatus(String status);
    
    /**
     * Kullanıcının belirli statüdeki transfer sayısı.
     */
    long countByUserIdAndStatus(Long userId, String status);
    
    /**
     * Transfer servisi için belirli statüdeki transfer sayısı.
     */
    long countByTransferServiceIdAndStatus(Long transferServiceId, String status);
    
    /**
     * Bugün yapılacak transfer sayısı (CONFIRMED).
     */
    @Query("SELECT COUNT(tr) FROM TransferReservation tr " +
           "WHERE DATE(tr.transferDateTime) = CURRENT_DATE " +
           "AND tr.status = 'CONFIRMED'")
    long countTodayTransfers();
    
    /**
     * Belirli tarih aralığındaki toplam ciro (CONFIRMED transferler).
     */
    @Query("SELECT COALESCE(SUM(tr.totalPrice), 0) FROM TransferReservation tr " +
           "WHERE tr.status = 'CONFIRMED' " +
           "AND tr.transferDateTime BETWEEN :start AND :end")
    java.math.BigDecimal calculateTotalRevenueForPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    /**
     * Transfer servisi için toplam ciro (CONFIRMED transferler).
     */
    @Query("SELECT COALESCE(SUM(tr.totalPrice), 0) FROM TransferReservation tr " +
           "WHERE tr.transferService.id = :serviceId " +
           "AND tr.status = 'CONFIRMED'")
    java.math.BigDecimal calculateTotalRevenueByService(@Param("serviceId") Long serviceId);
    
    // ==================== Pagination Desteği ====================
    
    /**
     * Kullanıcının tüm transferleri (pagination ile).
     */
    Page<TransferReservation> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Transfer servisi için tüm rezervasyonlar (pagination ile).
     */
    Page<TransferReservation> findByTransferServiceId(Long transferServiceId, Pageable pageable);
    
    /**
     * Tarih aralığında oluşturulmuş rezervasyonlar (pagination ile).
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "WHERE tr.createdAt BETWEEN :start AND :end " +
           "ORDER BY tr.createdAt DESC")
    Page<TransferReservation> findByCreatedAtBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);
    
    /**
     * Transfer tarihi aralığındaki rezervasyonlar (pagination ile).
     */
    @Query("SELECT tr FROM TransferReservation tr " +
           "WHERE tr.transferDateTime BETWEEN :start AND :end " +
           "ORDER BY tr.transferDateTime ASC")
    Page<TransferReservation> findByTransferDateTimeBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    
    /**
     * @deprecated Use findByUserId(Long userId, Pageable pageable) instead.
     * This method loads all records into memory which is not scalable.
     */
    @Deprecated
    List<TransferReservation> findByUserId(Long userId);
    
    /**
     * Basic find by reservation number (without JOIN FETCH).
     * For better performance, use findByReservationNumberWithDetails instead.
     */
    Optional<TransferReservation> findByReservationNumber(String reservationNumber);
}

