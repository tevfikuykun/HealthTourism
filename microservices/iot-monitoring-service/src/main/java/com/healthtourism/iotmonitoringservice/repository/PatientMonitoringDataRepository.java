package com.healthtourism.iotmonitoringservice.repository;

import com.healthtourism.iotmonitoringservice.entity.PatientMonitoringData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PatientMonitoringDataRepository extends JpaRepository<PatientMonitoringData, Long> {
    List<PatientMonitoringData> findByUserIdOrderByRecordedAtDesc(Long userId);
    List<PatientMonitoringData> findByReservationIdOrderByRecordedAtDesc(Long reservationId);
    List<PatientMonitoringData> findByDoctorIdOrderByRecordedAtDesc(Long doctorId);
    List<PatientMonitoringData> findByUserIdAndRecordedAtBetween(
            Long userId, LocalDateTime start, LocalDateTime end);
    List<PatientMonitoringData> findByAlertStatus(String alertStatus);
    List<PatientMonitoringData> findByUserIdAndAlertStatus(Long userId, String alertStatus);
    
    @Query("SELECT p FROM PatientMonitoringData p WHERE p.userId = :userId AND p.recordedAt >= :since ORDER BY p.recordedAt DESC")
    List<PatientMonitoringData> findRecentByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
