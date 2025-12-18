package com.healthtourism.costpredictorservice.repository;

import com.healthtourism.costpredictorservice.entity.CostPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CostPredictionRepository extends JpaRepository<CostPrediction, Long> {
    List<CostPrediction> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<CostPrediction> findByHospitalIdAndProcedureType(Long hospitalId, String procedureType);
}
