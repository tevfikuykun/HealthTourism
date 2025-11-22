package com.healthtourism.telemedicineservice.repository;
import com.healthtourism.telemedicineservice.entity.TelemedicineConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TelemedicineConsultationRepository extends JpaRepository<TelemedicineConsultation, Long> {
    List<TelemedicineConsultation> findByUserId(Long userId);
    List<TelemedicineConsultation> findByDoctorId(Long doctorId);
    List<TelemedicineConsultation> findByStatus(String status);
    @Query("SELECT t FROM TelemedicineConsultation t WHERE t.userId = :userId ORDER BY t.consultationDate DESC")
    List<TelemedicineConsultation> findByUserIdOrderByConsultationDateDesc(@Param("userId") Long userId);
    @Query("SELECT t FROM TelemedicineConsultation t WHERE t.doctorId = :doctorId AND t.consultationDate BETWEEN :startDate AND :endDate AND t.status != 'CANCELLED'")
    List<TelemedicineConsultation> findConflictingConsultations(@Param("doctorId") Long doctorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}

