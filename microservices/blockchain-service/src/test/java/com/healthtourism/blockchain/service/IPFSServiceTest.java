package com.healthtourism.blockchain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IPFSServiceTest {

    @InjectMocks
    private IPFSService ipfsService;

    @Test
    void testUploadToIPFS() {
        // Given
        String testData = "Test medical record data";

        // When
        String cid = ipfsService.uploadToIPFS(testData);

        // Then
        assertNotNull(cid);
        assertTrue(cid.startsWith("Qm"));
    }

    @Test
    void testGetGatewayUrl() {
        // Given
        String cid = "QmTest123";

        // When
        String gatewayUrl = ipfsService.getGatewayUrl(cid);

        // Then
        assertNotNull(gatewayUrl);
        assertTrue(gatewayUrl.contains(cid));
    }
}
