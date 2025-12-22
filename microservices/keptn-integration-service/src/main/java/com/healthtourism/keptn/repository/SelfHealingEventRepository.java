package com.healthtourism.keptn.repository;

import com.healthtourism.keptn.entity.SelfHealingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SelfHealingEventRepository extends JpaRepository<SelfHealingEvent, String> {
    List<SelfHealingEvent> findByTypeOrderByTimestampDesc(String type);
    List<SelfHealingEvent> findByServiceNameOrderByTimestampDesc(String serviceName);
    List<SelfHealingEvent> findByTimestampAfterOrderByTimestampDesc(LocalDateTime timestamp);
    List<SelfHealingEvent> findAllByOrderByTimestampDesc();
}






