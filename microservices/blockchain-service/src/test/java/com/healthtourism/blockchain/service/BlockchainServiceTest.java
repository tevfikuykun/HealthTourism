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
class BlockchainServiceTest {

    @Mock
    private BlockchainRecordRepository recordRepository;

    @InjectMocks
    private BlockchainService blockchainService;

    @Test
    void testCreateBlock() {
        // Given
        Map<String, Object> data = new HashMap<>();
        data.put("treatment", "Dental Implant");
        data.put("amount", 5000);

        BlockchainRecord record = new BlockchainRecord();
        record.setBlockHash("hash123");
        when(recordRepository.findTopByOrderByBlockIndexDesc()).thenReturn(Optional.empty());
        when(recordRepository.save(any(BlockchainRecord.class))).thenReturn(record);

        // When
        BlockchainRecord result = blockchainService.createBlock(
            data, BlockchainRecord.RecordType.MEDICAL_TREATMENT, "123", "1"
        );

        // Then
        assertNotNull(result);
        assertNotNull(result.getBlockHash());
        verify(recordRepository, times(1)).save(any(BlockchainRecord.class));
    }

    @Test
    void testVerifyChainIntegrity() {
        // Given
        when(recordRepository.findAllByOrderByBlockIndexAsc()).thenReturn(java.util.Collections.emptyList());

        // When
        Boolean result = blockchainService.verifyChainIntegrity();

        // Then
        assertNotNull(result);
    }
}
