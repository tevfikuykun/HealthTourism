package com.healthtourism.blockchain.service;

import com.healthtourism.blockchain.entity.BlockchainRecord;
import com.healthtourism.blockchain.repository.BlockchainRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockchainHealthCheckServiceTest {

    @Mock
    private BlockchainService blockchainService;

    @Mock
    private BlockchainRecordRepository recordRepository;

    @InjectMocks
    private BlockchainHealthCheckService healthCheckService;

    @Test
    void testPerformHealthCheck() {
        // Given
        when(blockchainService.verifyChainIntegrity()).thenReturn(true);
        when(recordRepository.count()).thenReturn(10L);
        when(blockchainService.calculateDataHash(anyString())).thenReturn("a1b2c3d4e5f6...");

        // When
        Map<String, Object> healthReport = healthCheckService.performHealthCheck();

        // Then
        assertNotNull(healthReport);
        assertTrue(healthReport.containsKey("timestamp"));
        assertTrue(healthReport.containsKey("status"));
        assertTrue(healthReport.containsKey("chainIntegrity"));
    }

    @Test
    void testCheckChainIntegrity() {
        // Given
        when(blockchainService.verifyChainIntegrity()).thenReturn(true);

        // When
        boolean integrity = healthCheckService.checkChainIntegrity();

        // Then
        assertTrue(integrity);
    }

    @Test
    void testCheckDatabaseConnectivity() {
        // Given
        when(recordRepository.count()).thenReturn(5L);

        // When
        boolean connectivity = healthCheckService.checkDatabaseConnectivity();

        // Then
        assertTrue(connectivity);
    }

    @Test
    void testGetChainLength() {
        // Given
        when(recordRepository.count()).thenReturn(100L);

        // When
        long length = healthCheckService.getChainLength();

        // Then
        assertEquals(100L, length);
    }
}
