package com.healthtourism.blockchain.controller;

import com.healthtourism.blockchain.entity.BlockchainRecord;
import com.healthtourism.blockchain.service.BlockchainService;
import com.healthtourism.blockchain.service.IPFSService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blockchain")
@CrossOrigin(origins = "*")
public class BlockchainController {
    
    @Autowired
    private BlockchainService blockchainService;
    
    @Autowired(required = false)
    private IPFSService ipfsService;
    
    @Autowired(required = false)
    private com.healthtourism.blockchain.service.PolygonLayer2Service polygonLayer2Service;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @PostMapping("/create")
    public ResponseEntity<BlockchainRecord> createBlock(
            @RequestBody Map<String, Object> data,
            @RequestParam BlockchainRecord.RecordType recordType,
            @RequestParam String recordId,
            @RequestParam String userId,
            @RequestParam(required = false) String dataReference,
            @RequestParam(required = false, defaultValue = "false") boolean useIPFS) {
        
        // If useIPFS is true and IPFS service is available, upload to IPFS
        if (useIPFS && ipfsService != null) {
            try {
                String dataJson = objectMapper.writeValueAsString(data);
                String ipfsCid = ipfsService.uploadToIPFS(dataJson);
                ipfsService.pinToIPFS(ipfsCid); // Pin to ensure persistence
                dataReference = "ipfs://" + ipfsCid;
            } catch (Exception e) {
                // Fallback to default if IPFS fails
                if (dataReference == null || dataReference.isEmpty()) {
                    dataReference = "off-chain://" + recordType + "/" + recordId;
                }
            }
        } else if (dataReference == null || dataReference.isEmpty()) {
            // If dataReference not provided, generate one (in production, store data off-chain first)
            dataReference = "off-chain://" + recordType + "/" + recordId;
        }
        
        return ResponseEntity.ok(blockchainService.createBlock(data, recordType, recordId, userId, dataReference));
    }
    
    /**
     * Upload data to IPFS and create blockchain record
     */
    @PostMapping("/create-with-ipfs")
    public ResponseEntity<BlockchainRecord> createBlockWithIPFS(
            @RequestBody Map<String, Object> data,
            @RequestParam BlockchainRecord.RecordType recordType,
            @RequestParam String recordId,
            @RequestParam String userId) {
        
        if (ipfsService == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            String dataJson = objectMapper.writeValueAsString(data);
            String ipfsCid = ipfsService.uploadToIPFS(dataJson);
            ipfsService.pinToIPFS(ipfsCid);
            String dataReference = "ipfs://" + ipfsCid;
            
            return ResponseEntity.ok(blockchainService.createBlock(data, recordType, recordId, userId, dataReference));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Retrieve data from IPFS using CID
     */
    @GetMapping("/ipfs/{cid}")
    public ResponseEntity<Map<String, String>> retrieveFromIPFS(@PathVariable String cid) {
        if (ipfsService == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            String data = ipfsService.retrieveFromIPFS(cid);
            return ResponseEntity.ok(Map.of("cid", cid, "data", data, "gatewayUrl", ipfsService.getGatewayUrl(cid)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Verify data integrity by comparing stored hash with recalculated hash
     */
    @PostMapping("/verify-data")
    public ResponseEntity<Boolean> verifyDataIntegrity(
            @RequestParam String blockHash,
            @RequestBody Map<String, Object> data) {
        try {
            BlockchainRecord record = blockchainService.getRecordByHash(blockHash);
            
            // Recalculate hash from provided data
            String dataJson = objectMapper.writeValueAsString(data);
            String calculatedHash = blockchainService.calculateDataHash(dataJson);
            
            // Compare with stored hash
            boolean isValid = calculatedHash.equals(record.getDataHash());
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BlockchainRecord>> getRecordsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(blockchainService.getRecordsByUser(userId));
    }
    
    @GetMapping("/type/{recordType}")
    public ResponseEntity<List<BlockchainRecord>> getRecordsByType(@PathVariable BlockchainRecord.RecordType recordType) {
        return ResponseEntity.ok(blockchainService.getRecordsByType(recordType));
    }
    
    @GetMapping("/hash/{blockHash}")
    public ResponseEntity<BlockchainRecord> getRecordByHash(@PathVariable String blockHash) {
        try {
            return ResponseEntity.ok(blockchainService.getRecordByHash(blockHash));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/verify")
    public ResponseEntity<Boolean> verifyChainIntegrity() {
        return ResponseEntity.ok(blockchainService.verifyChainIntegrity());
    }
    
    /**
     * Store record on Polygon Layer 2 (cost-effective)
     */
    @PostMapping("/polygon/store")
    public ResponseEntity<Map<String, Object>> storeOnPolygon(
            @RequestParam String dataHash,
            @RequestParam String ipfsHash,
            @RequestBody(required = false) Map<String, Object> metadata) {
        if (polygonLayer2Service == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(polygonLayer2Service.storeRecordOnPolygon(dataHash, ipfsHash, metadata));
    }
    
    /**
     * Compare costs between Ethereum and Polygon
     */
    @GetMapping("/polygon/compare-costs")
    public ResponseEntity<Map<String, Object>> compareCosts(@RequestParam String dataHash) {
        if (polygonLayer2Service == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(polygonLayer2Service.compareCosts(dataHash));
    }
    
    /**
     * Get Polygon network status
     */
    @GetMapping("/polygon/status")
    public ResponseEntity<Map<String, Object>> getPolygonStatus() {
        if (polygonLayer2Service == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(polygonLayer2Service.getNetworkStatus());
    }
}
