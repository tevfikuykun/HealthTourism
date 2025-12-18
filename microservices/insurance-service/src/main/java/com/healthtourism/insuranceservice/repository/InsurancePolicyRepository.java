package com.healthtourism.insuranceservice.repository;

import com.healthtourism.insuranceservice.entity.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Long> {
    List<InsurancePolicy> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<InsurancePolicy> findByReservationId(Long reservationId);
    Optional<InsurancePolicy> findByPolicyNumber(String policyNumber);
    List<InsurancePolicy> findByStatus(String status);
    List<InsurancePolicy> findByUserIdAndStatus(Long userId, String status);
}
