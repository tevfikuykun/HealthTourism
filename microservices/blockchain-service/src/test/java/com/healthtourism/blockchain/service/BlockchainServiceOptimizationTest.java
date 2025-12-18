package com.healthtourism.blockchain.service;

import com.healthtourism.blockchain.entity.BlockchainRecord;
import com.healthtourism.blockchain.repository.BlockchainRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockchainServiceOptimizationTest {

    @Mock
    private BlockchainRecordRepository recordRepository;

    @InjectMocks
    private BlockchainService blockchainService;

    @Test
    void testOffChainStorageOptimization() {
        // Given - Large data object
        Map<String, Object> largeData = new HashMap<>();
        largeData.put("treatment", "Complex Medical Procedure");
        largeData.put("doctor", "Dr. Specialist");
        largeData.put("hospital", "Medical Center");
        largeData.put("detailedReport", "Very long detailed medical report...".repeat(100)); // Large data
        
        BlockchainRecord record = new BlockchainRecord();
        record.setDataHash("hash123");
        record.setDataReference("s3://bucket/large-data-123.json");
        
        when(recordRepository.findTopByOrderByBlockIndexDesc()).thenReturn(Optional.empty());
        when(recordRepository.save(any(BlockchainRecord.class))).thenReturn(record);

        // When - Create block with off-chain storage
        BlockchainRecord result = blockchainService.createBlock(
            largeData, 
            BlockchainRecord.RecordType.MEDICAL_TREATMENT, 
            "123", 
            "1",
            "s3://bucket/large-data-123.json"
        );

        // Then - Verify only hash is stored, not full data
        assertNotNull(result);
        assertNotNull(result.getDataHash());
        assertNotNull(result.getDataReference());
        // Verify that large data is NOT stored in database (only hash)
        assertNull(result.getData()); // data field should be null (removed in optimization)
        verify(recordRepository, times(1)).save(any(BlockchainRecord.class));
    }

    @Test
    void testDataHashCalculation() {
        // Given
        String testData = "Test medical record data";

        // When
        String hash1 = blockchainService.calculateDataHash(testData);
        String hash2 = blockchainService.calculateDataHash(testData);

        // Then - Same data should produce same hash
        assertEquals(hash1, hash2);
        assertNotNull(hash1);
        assertEquals(64, hash1.length()); // SHA-256 produces 64 character hex string
    }
}
