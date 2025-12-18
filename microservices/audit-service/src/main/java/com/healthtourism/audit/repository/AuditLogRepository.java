package com.healthtourism.audit.repository;

import com.healthtourism.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserIdOrderByTimestampDesc(Long userId);
    List<AuditLog> findByResourceTypeAndResourceIdOrderByTimestampDesc(AuditLog.ResourceType resourceType, String resourceId);
    List<AuditLog> findByActionAndTimestampBetween(AuditLog.Action action, LocalDateTime start, LocalDateTime end);
    List<AuditLog> findByUserIdAndResourceTypeOrderByTimestampDesc(Long userId, AuditLog.ResourceType resourceType);
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.resourceType = :resourceType AND a.resourceId = :resourceId")
    Long countAccessesByResource(AuditLog.ResourceType resourceType, String resourceId);
    
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.resourceType = :resourceType AND a.timestamp >= :since")
    List<AuditLog> findRecentAccessesByUser(Long userId, AuditLog.ResourceType resourceType, LocalDateTime since);
    
    List<AuditLog> findByTimestampBefore(LocalDateTime timestamp);
}
