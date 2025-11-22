package com.healthtourism.appointmentcalendarservice.repository;
import com.healthtourism.appointmentcalendarservice.entity.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
    List<AppointmentSlot> findByDoctorId(Long doctorId);
    List<AppointmentSlot> findByDoctorIdAndIsAvailableTrue(Long doctorId);
    @Query("SELECT a FROM AppointmentSlot a WHERE a.doctorId = :doctorId AND a.startTime >= :startDate AND a.startTime <= :endDate ORDER BY a.startTime ASC")
    List<AppointmentSlot> findByDoctorIdAndDateRange(@Param("doctorId") Long doctorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}

