package com.healthtourism.blockchain.repository;

import com.healthtourism.blockchain.entity.BlockchainRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockchainRecordRepository extends JpaRepository<BlockchainRecord, Long> {
    Optional<BlockchainRecord> findByBlockHash(String blockHash);
    List<BlockchainRecord> findByUserIdOrderByBlockIndexDesc(String userId);
    List<BlockchainRecord> findByRecordTypeOrderByBlockIndexDesc(BlockchainRecord.RecordType recordType);
    Optional<BlockchainRecord> findTopByOrderByBlockIndexDesc();
    List<BlockchainRecord> findAllByOrderByBlockIndexAsc();
}
