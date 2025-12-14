package com.healthtourism.influencerservice.service;
import com.healthtourism.influencerservice.entity.Campaign;
import com.healthtourism.influencerservice.entity.Influencer;
import com.healthtourism.influencerservice.repository.CampaignRepository;
import com.healthtourism.influencerservice.repository.InfluencerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InfluencerService {
    @Autowired
    private InfluencerRepository influencerRepository;
    @Autowired
    private CampaignRepository campaignRepository;
    
    public Influencer registerInfluencer(Influencer influencer) {
        return influencerRepository.save(influencer);
    }
    
    public Influencer approveInfluencer(Long id) {
        Influencer influencer = influencerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Influencer bulunamadı"));
        influencer.setStatus("APPROVED");
        return influencerRepository.save(influencer);
    }
    
    public Campaign createCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }
    
    public List<Campaign> getCampaignsByInfluencer(Long influencerId) {
        return campaignRepository.findByInfluencerId(influencerId);
    }
    
    public Campaign updateCampaignPerformance(Long campaignId, Integer clicks, Integer conversions) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign bulunamadı"));
        campaign.setClicks(clicks);
        campaign.setConversions(conversions);
        return campaignRepository.save(campaign);
    }
    
    public Double calculateCommission(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign bulunamadı"));
        return campaign.getBudget() * (campaign.getCommissionRate() / 100);
    }
}

