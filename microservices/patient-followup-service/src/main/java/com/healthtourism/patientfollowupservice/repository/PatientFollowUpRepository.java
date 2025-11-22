package com.healthtourism.patientfollowupservice.repository;
import com.healthtourism.patientfollowupservice.entity.PatientFollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PatientFollowUpRepository extends JpaRepository<PatientFollowUp, Long> {
    List<PatientFollowUp> findByUserId(Long userId);
    List<PatientFollowUp> findByDoctorId(Long doctorId);
    List<PatientFollowUp> findByReservationId(Long reservationId);
    List<PatientFollowUp> findByStatus(String status);
    @Query("SELECT p FROM PatientFollowUp p WHERE p.userId = :userId ORDER BY p.followUpDate DESC")
    List<PatientFollowUp> findByUserIdOrderByFollowUpDateDesc(@Param("userId") Long userId);
}

