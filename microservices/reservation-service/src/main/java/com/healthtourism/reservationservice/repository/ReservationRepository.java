package com.healthtourism.reservationservice.repository;

import com.healthtourism.reservationservice.entity.Reservation;
import com.healthtourism.reservationservice.entity.ReservationStatus;
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
 * Reservation Repository
 * 
 * Professional repository with:
 * - Pagination support
 * - Soft delete filtering (deleted = false) via @Where annotation on Entity
 * - Custom queries for business needs
 * - Conflict detection queries
 * 
 * Note: @Where annotation on Reservation entity automatically filters deleted records,
 * so all queries implicitly exclude deleted reservations.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    /**
     * Find reservation by reservation number
     */
    Optional<Reservation> findByReservationNumber(String reservationNumber);
    
    /**
     * Find reservations by user ID with pagination
     */
    Page<Reservation> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Find reservations by user ID (without pagination)
     */
    List<Reservation> findByUserId(Long userId);
    
    /**
     * Find reservations by status with pagination
     */
    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);
    
    /**
     * Find reservations by doctor ID with pagination
     */
    Page<Reservation> findByDoctorId(Long doctorId, Pageable pageable);
    
    /**
     * Find reservations by hospital ID with pagination
     */
    Page<Reservation> findByHospitalId(Long hospitalId, Pageable pageable);
    
    /**
     * Check for appointment conflicts
     * 
     * Business Rule: A doctor cannot have two appointments at the same time
     * This query finds any existing reservations that overlap with the given appointment time.
     * 
     * @param doctorId Doctor ID
     * @param appointmentDate Proposed appointment date
     * @param appointmentDurationMinutes Duration of appointment in minutes (default: 60)
     * @param excludeReservationId Reservation ID to exclude from conflict check (for updates)
     * @return List of conflicting reservations
     */
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.doctorId = :doctorId
        AND r.status IN :activeStatuses
        AND r.appointmentDate BETWEEN :startTime AND :endTime
        AND (:excludeReservationId IS NULL OR r.id != :excludeReservationId)
        """)
    List<Reservation> findConflictingAppointments(
        @Param("doctorId") Long doctorId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("activeStatuses") List<ReservationStatus> activeStatuses,
        @Param("excludeReservationId") Long excludeReservationId
    );
    
    /**
     * Check if doctor has appointment at specific time
     * 
     * @param doctorId Doctor ID
     * @param appointmentDate Appointment date
     * @param excludeReservationId Reservation ID to exclude (for updates)
     * @return true if conflict exists
     */
    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r
        WHERE r.doctorId = :doctorId
        AND r.status IN :activeStatuses
        AND r.appointmentDate = :appointmentDate
        AND (:excludeReservationId IS NULL OR r.id != :excludeReservationId)
        """)
    boolean existsByDoctorIdAndAppointmentDate(
        @Param("doctorId") Long doctorId,
        @Param("appointmentDate") LocalDateTime appointmentDate,
        @Param("activeStatuses") List<ReservationStatus> activeStatuses,
        @Param("excludeReservationId") Long excludeReservationId
    );
    
    /**
     * Count reservations by user for a specific date
     * Business Rule: Check if user already has appointment on same day
     */
    @Query("""
        SELECT COUNT(r) FROM Reservation r
        WHERE r.userId = :userId
        AND r.status IN :activeStatuses
        AND DATE(r.appointmentDate) = DATE(:appointmentDate)
        """)
    long countByUserIdAndAppointmentDate(
        @Param("userId") Long userId,
        @Param("appointmentDate") LocalDateTime appointmentDate,
        @Param("activeStatuses") List<ReservationStatus> activeStatuses
    );
    
    /**
     * Find upcoming reservations by user
     */
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.userId = :userId
        AND r.appointmentDate >= :now
        AND r.status IN :activeStatuses
        ORDER BY r.appointmentDate ASC
        """)
    List<Reservation> findUpcomingReservationsByUser(
        @Param("userId") Long userId,
        @Param("now") LocalDateTime now,
        @Param("activeStatuses") List<ReservationStatus> activeStatuses
    );
    
    /**
     * Find reservations by date range
     */
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.appointmentDate BETWEEN :startDate AND :endDate
        AND r.status IN :statuses
        ORDER BY r.appointmentDate ASC
        """)
    List<Reservation> findByAppointmentDateBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("statuses") List<ReservationStatus> statuses
    );
    
    /**
     * Count reservations by status
     */
    long countByStatus(ReservationStatus status);
}
