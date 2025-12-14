package com.healthtourism.affiliateservice.controller;
import com.healthtourism.affiliateservice.entity.Affiliate;
import com.healthtourism.affiliateservice.entity.Referral;
import com.healthtourism.affiliateservice.service.AffiliateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/affiliate")
@CrossOrigin(origins = "*")
public class AffiliateController {
    @Autowired
    private AffiliateService service;
    
    @PostMapping("/register")
    public ResponseEntity<Affiliate> registerAffiliate(@RequestBody Map<String, Long> request) {
        return ResponseEntity.ok(service.registerAffiliate(request.get("userId")));
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<Affiliate> getAffiliateByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getAffiliateByCode(code));
    }
    
    @PostMapping("/track/click")
    public ResponseEntity<Referral> trackClick(@RequestBody Map<String, String> request) {
        Long userId = request.get("userId") != null ? Long.parseLong(request.get("userId")) : null;
        return ResponseEntity.ok(service.trackClick(request.get("referralCode"), userId));
    }
    
    @PostMapping("/track/conversion")
    public ResponseEntity<Referral> trackConversion(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(service.trackConversion(
            Long.parseLong(request.get("referralId").toString()),
            Long.parseLong(request.get("reservationId").toString()),
            Double.parseDouble(request.get("amount").toString())
        ));
    }
    
    @GetMapping("/referrals/{affiliateId}")
    public ResponseEntity<List<Referral>> getReferrals(@PathVariable Long affiliateId) {
        return ResponseEntity.ok(service.getReferralsByAffiliate(affiliateId));
    }
}

