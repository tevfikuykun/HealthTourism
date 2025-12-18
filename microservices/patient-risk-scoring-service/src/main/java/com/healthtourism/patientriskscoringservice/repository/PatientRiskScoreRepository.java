package com.healthtourism.patientriskscoringservice.repository;

import com.healthtourism.patientriskscoringservice.entity.PatientRiskScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRiskScoreRepository extends JpaRepository<PatientRiskScore, Long> {
    List<PatientRiskScore> findByUserIdAndReservationIdOrderByCalculatedAtDesc(Long userId, Long reservationId);
    List<PatientRiskScore> findByRequiresDoctorAlertTrue();
    List<PatientRiskScore> findByUserIdOrderByCalculatedAtDesc(Long userId);
    List<PatientRiskScore> findByScoreCategory(String scoreCategory);
}
