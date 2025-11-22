package com.healthtourism.monitoringservice.repository;

import com.healthtourism.monitoringservice.entity.ServiceHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceHealthRepository extends JpaRepository<ServiceHealth, Long> {
    Optional<ServiceHealth> findFirstByServiceNameOrderByCheckedAtDesc(String serviceName);
    List<ServiceHealth> findByServiceNameOrderByCheckedAtDesc(String serviceName);
}

