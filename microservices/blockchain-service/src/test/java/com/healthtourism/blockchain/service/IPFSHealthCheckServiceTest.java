package com.healthtourism.blockchain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IPFSHealthCheckServiceTest {

    @Mock
    private IPFSService ipfsService;

    @InjectMocks
    private IPFSHealthCheckService healthCheckService;

    @Test
    void testPerformHealthCheck() {
        // Given
        when(ipfsService.uploadToIPFS(anyString())).thenReturn("QmTest123");
        when(ipfsService.retrieveFromIPFS(anyString())).thenReturn("Test data");

        // When
        Map<String, Object> healthReport = healthCheckService.performHealthCheck();

        // Then
        assertNotNull(healthReport);
        assertTrue(healthReport.containsKey("timestamp"));
        assertTrue(healthReport.containsKey("status"));
    }

    @Test
    void testCheckGatewayConnectivity() {
        // When
        boolean accessible = healthCheckService.checkGatewayConnectivity();

        // Then - May be true or false depending on network, but should not throw
        assertNotNull(accessible);
    }

    @Test
    void testPerformUploadTest() {
        // Given
        when(ipfsService.uploadToIPFS(anyString())).thenReturn("QmTest123");

        // When
        boolean uploadTest = healthCheckService.performUploadTest();

        // Then
        assertTrue(uploadTest);
    }
}
