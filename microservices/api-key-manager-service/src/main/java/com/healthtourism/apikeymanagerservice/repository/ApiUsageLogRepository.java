package com.healthtourism.apikeymanagerservice.repository;

import com.healthtourism.apikeymanagerservice.entity.ApiUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ApiUsageLogRepository extends JpaRepository<ApiUsageLog, Long> {
    List<ApiUsageLog> findByApiKeyId(Long apiKeyId);
    
    @Query("SELECT COUNT(u) FROM ApiUsageLog u WHERE u.apiKeyId = :apiKeyId AND u.timestamp >= :startDate")
    Long countByApiKeyIdAndTimestampAfter(@Param("apiKeyId") Long apiKeyId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT SUM(u.responseTime) / COUNT(u) FROM ApiUsageLog u WHERE u.apiKeyId = :apiKeyId AND u.timestamp >= :startDate")
    Double getAverageResponseTime(@Param("apiKeyId") Long apiKeyId, @Param("startDate") LocalDateTime startDate);
}
