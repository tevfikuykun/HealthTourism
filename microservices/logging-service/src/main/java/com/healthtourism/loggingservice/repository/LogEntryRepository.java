package com.healthtourism.loggingservice.repository;

import com.healthtourism.loggingservice.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findByServiceNameAndLevelAndTimestampBetween(
        String serviceName, String level, LocalDateTime start, LocalDateTime end
    );
    List<LogEntry> findByServiceNameOrderByTimestampDesc(String serviceName);
    List<LogEntry> findByLevelAndTimestampBetween(String level, LocalDateTime start, LocalDateTime end);
}

