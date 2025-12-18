package com.healthtourism.blockchain.controller;

import com.healthtourism.blockchain.service.BlockchainHealthCheckService;
import com.healthtourism.blockchain.service.IPFSHealthCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthCheckController.class)
class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPFSHealthCheckService ipfsHealthCheckService;

    @MockBean
    private BlockchainHealthCheckService blockchainHealthCheckService;

    @Test
    void testIPFSHealthCheck() throws Exception {
        // Given
        Map<String, Object> healthReport = Map.of(
            "healthy", true,
            "status", "HEALTHY",
            "timestamp", java.time.LocalDateTime.now()
        );
        when(ipfsHealthCheckService.performHealthCheck()).thenReturn(healthReport);

        // When & Then
        mockMvc.perform(get("/api/health/ipfs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.healthy").value(true));
    }

    @Test
    void testBlockchainHealthCheck() throws Exception {
        // Given
        Map<String, Object> healthReport = Map.of(
            "healthy", true,
            "status", "HEALTHY",
            "timestamp", java.time.LocalDateTime.now()
        );
        when(blockchainHealthCheckService.performHealthCheck()).thenReturn(healthReport);

        // When & Then
        mockMvc.perform(get("/api/health/blockchain"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.healthy").value(true));
    }

    @Test
    void testProductionReadinessCheck() throws Exception {
        // Given
        when(ipfsHealthCheckService.isProductionReady()).thenReturn(true);
        when(blockchainHealthCheckService.isProductionReady()).thenReturn(true);
        when(blockchainHealthCheckService.validateNetworkConfiguration()).thenReturn(
            Map.of("networkType", "testnet", "isTestnet", true)
        );

        // When & Then
        mockMvc.perform(get("/api/health/production-ready"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.overallReady").value(true));
    }
}
