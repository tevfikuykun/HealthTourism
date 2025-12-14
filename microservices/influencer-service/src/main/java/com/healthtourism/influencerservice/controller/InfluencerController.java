package com.healthtourism.influencerservice.controller;
import com.healthtourism.influencerservice.entity.Campaign;
import com.healthtourism.influencerservice.entity.Influencer;
import com.healthtourism.influencerservice.service.InfluencerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/influencers")
@CrossOrigin(origins = "*")
public class InfluencerController {
    @Autowired
    private InfluencerService service;
    
    @PostMapping("/register")
    public ResponseEntity<Influencer> registerInfluencer(@RequestBody Influencer influencer) {
        return ResponseEntity.ok(service.registerInfluencer(influencer));
    }
    
    @PostMapping("/{id}/approve")
    public ResponseEntity<Influencer> approveInfluencer(@PathVariable Long id) {
        return ResponseEntity.ok(service.approveInfluencer(id));
    }
    
    @PostMapping("/campaigns")
    public ResponseEntity<Campaign> createCampaign(@RequestBody Campaign campaign) {
        return ResponseEntity.ok(service.createCampaign(campaign));
    }
    
    @GetMapping("/campaigns/{influencerId}")
    public ResponseEntity<List<Campaign>> getCampaigns(@PathVariable Long influencerId) {
        return ResponseEntity.ok(service.getCampaignsByInfluencer(influencerId));
    }
    
    @PutMapping("/campaigns/{id}/performance")
    public ResponseEntity<Campaign> updatePerformance(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        return ResponseEntity.ok(service.updateCampaignPerformance(
            id, request.get("clicks"), request.get("conversions")));
    }
    
    @GetMapping("/campaigns/{id}/commission")
    public ResponseEntity<Map<String, Double>> calculateCommission(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("commission", service.calculateCommission(id)));
    }
}

