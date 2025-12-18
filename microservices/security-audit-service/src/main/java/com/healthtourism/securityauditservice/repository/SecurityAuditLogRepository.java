package com.healthtourism.securityauditservice.repository;

import com.healthtourism.securityauditservice.entity.SecurityAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, Long> {
    
    long countByUserIdAndAuthorizedAndTimestampAfter(String userId, Boolean authorized, LocalDateTime timestamp);
    
    @Query("SELECT COUNT(s) FROM SecurityAuditLog s WHERE s.userId = :userId AND s.authorized = false AND s.timestamp >= :since")
    long countSuspiciousAttempts(@Param("userId") String userId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(s) FROM SecurityAuditLog s WHERE s.resourceType = :resourceType AND s.authorized = false AND s.timestamp >= :since")
    long countUnauthorizedAccessByResourceType(@Param("resourceType") String resourceType, @Param("since") LocalDateTime since);
}
