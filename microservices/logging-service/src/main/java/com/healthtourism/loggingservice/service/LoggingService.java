package com.healthtourism.loggingservice.service;

import com.healthtourism.loggingservice.dto.LogEntryDTO;
import com.healthtourism.loggingservice.entity.LogEntry;
import com.healthtourism.loggingservice.repository.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoggingService {
    
    @Autowired
    private LogEntryRepository logEntryRepository;
    
    public LogEntryDTO log(LogEntryDTO dto) {
        LogEntry entry = new LogEntry();
        entry.setServiceName(dto.getServiceName());
        entry.setLevel(dto.getLevel());
        entry.setMessage(dto.getMessage());
        entry.setException(dto.getException());
        entry.setUserId(dto.getUserId());
        entry.setRequestId(dto.getRequestId());
        entry = logEntryRepository.save(entry);
        return convertToDTO(entry);
    }
    
    public List<LogEntryDTO> getLogs(String serviceName, String level, LocalDateTime start, LocalDateTime end) {
        if (level != null) {
            return logEntryRepository.findByServiceNameAndLevelAndTimestampBetween(serviceName, level, start, end)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        }
        return logEntryRepository.findByServiceNameOrderByTimestampDesc(serviceName)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<LogEntryDTO> getErrorLogs(LocalDateTime start, LocalDateTime end) {
        return logEntryRepository.findByLevelAndTimestampBetween("ERROR", start, end)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private LogEntryDTO convertToDTO(LogEntry entry) {
        return new LogEntryDTO(
            entry.getId(),
            entry.getServiceName(),
            entry.getLevel(),
            entry.getMessage(),
            entry.getException(),
            entry.getUserId(),
            entry.getRequestId(),
            entry.getTimestamp()
        );
    }
}

