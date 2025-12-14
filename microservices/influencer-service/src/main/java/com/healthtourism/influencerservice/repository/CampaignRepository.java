package com.healthtourism.influencerservice.repository;
import com.healthtourism.influencerservice.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByInfluencerId(Long influencerId);
    List<Campaign> findByStatus(String status);
}

