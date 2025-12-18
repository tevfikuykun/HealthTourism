package com.healthtourism.blockchain.service;

import com.healthtourism.blockchain.entity.BlockchainRecord;
import com.healthtourism.blockchain.repository.BlockchainRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Blockchain Service for Data Integrity
 * Creates immutable records of medical treatments and payments
 */
@Service
public class BlockchainService {
    
    @Autowired
    private BlockchainRecordRepository recordRepository;
    
    @Autowired(required = false)
    private IPFSService ipfsService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Transactional
    public BlockchainRecord createBlock(Object data, BlockchainRecord.RecordType recordType, String recordId, String userId, String dataReference) {
        // Get previous block
        BlockchainRecord previousBlock = getLatestBlock();
        String previousHash = previousBlock != null ? previousBlock.getBlockHash() : "0";
        Long blockIndex = previousBlock != null ? previousBlock.getBlockIndex() + 1 : 0L;
        
        // Create new block
        BlockchainRecord newBlock = new BlockchainRecord();
        newBlock.setPreviousHash(previousHash);
        newBlock.setBlockIndex(blockIndex);
        newBlock.setRecordType(recordType);
        newBlock.setRecordId(recordId);
        newBlock.setUserId(userId);
        newBlock.setTimestamp(LocalDateTime.now());
        newBlock.setIsValid(true);
        newBlock.setDataReference(dataReference); // Off-chain storage reference
        
        try {
            // Convert data to JSON string for hashing
            String dataJson = objectMapper.writeValueAsString(data);
            
            // Calculate data hash (off-chain storage - only hash stored)
            String dataHash = calculateDataHash(dataJson);
            newBlock.setDataHash(dataHash);
            
            // Store lightweight metadata if needed (optional)
            Map<String, Object> metadataMap = new HashMap<>();
            if (data instanceof Map) {
                Map<String, Object> dataMap = (Map<String, Object>) data;
                // Extract only essential fields for metadata
                metadataMap.put("recordType", recordType.toString());
                metadataMap.put("recordId", recordId);
                if (dataMap.containsKey("amount")) metadataMap.put("amount", dataMap.get("amount"));
                if (dataMap.containsKey("date")) metadataMap.put("date", dataMap.get("date"));
            }
            if (!metadataMap.isEmpty()) {
                newBlock.setMetadata(objectMapper.writeValueAsString(metadataMap));
            }
            
            // Calculate block hash (includes dataHash, not full data)
            String blockHash = calculateHash(newBlock);
            newBlock.setBlockHash(blockHash);
            
            // Generate signature (simplified - in production use proper digital signature)
            String signature = generateSignature(newBlock);
            newBlock.setSignature(signature);
            
            // Validate block
            if (validateBlock(newBlock, previousBlock)) {
                return recordRepository.save(newBlock);
            } else {
                throw new RuntimeException("Block validation failed");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create block", e);
        }
    }
    
    public List<BlockchainRecord> getRecordsByUser(String userId) {
        return recordRepository.findByUserIdOrderByBlockIndexDesc(userId);
    }
    
    public List<BlockchainRecord> getRecordsByType(BlockchainRecord.RecordType recordType) {
        return recordRepository.findByRecordTypeOrderByBlockIndexDesc(recordType);
    }
    
    public BlockchainRecord getRecordByHash(String blockHash) {
        return recordRepository.findByBlockHash(blockHash)
            .orElseThrow(() -> new RuntimeException("Block not found"));
    }
    
    public Boolean verifyChainIntegrity() {
        List<BlockchainRecord> allBlocks = recordRepository.findAllByOrderByBlockIndexAsc();
        
        if (allBlocks.isEmpty()) {
            return true; // Empty chain is valid
        }
        
        for (int i = 1; i < allBlocks.size(); i++) {
            BlockchainRecord currentBlock = allBlocks.get(i);
            BlockchainRecord previousBlock = allBlocks.get(i - 1);
            
            if (!validateBlock(currentBlock, previousBlock)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Calculate data hash (public method for verification)
     */
    public String calculateDataHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate data hash", e);
        }
    }
    
    private BlockchainRecord getLatestBlock() {
        return recordRepository.findTopByOrderByBlockIndexDesc()
            .orElse(null);
    }
    
    /**
     * Calculate block hash (includes dataHash, not full data - off-chain storage optimization)
     */
    private String calculateHash(BlockchainRecord block) {
        try {
            String data = block.getPreviousHash() + 
                         block.getBlockIndex() + 
                         block.getRecordType() + 
                         block.getRecordId() + 
                         block.getDataHash() + // Use hash instead of full data (off-chain)
                         (block.getDataReference() != null ? block.getDataReference() : "") +
                         block.getTimestamp().toString();
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hash", e);
        }
    }
    
    /**
     * Convert bytes to hex string (helper method)
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    private String generateSignature(BlockchainRecord block) {
        // Simplified signature - in production use proper digital signature (RSA, ECDSA, etc.)
        try {
            String data = block.getBlockHash() + block.getUserId() + block.getTimestamp().toString();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] signature = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(signature);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
    
    private Boolean validateBlock(BlockchainRecord block, BlockchainRecord previousBlock) {
        // Validate hash
        String calculatedHash = calculateHash(block);
        if (!calculatedHash.equals(block.getBlockHash())) {
            return false;
        }
        
        // Validate previous hash link
        if (previousBlock != null && !block.getPreviousHash().equals(previousBlock.getBlockHash())) {
            return false;
        }
        
        // Validate signature
        String calculatedSignature = generateSignature(block);
        if (!calculatedSignature.equals(block.getSignature())) {
            return false;
        }
        
        return true;
    }
}
