package com.healthtourism.healthrecordsservice.repository;

import com.healthtourism.healthrecordsservice.entity.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    List<HealthRecord> findByUserId(Long userId);
    List<HealthRecord> findByUserIdOrderByDateDesc(Long userId);
}

