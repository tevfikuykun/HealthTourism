package com.healthtourism.affiliateservice.service;
import com.healthtourism.affiliateservice.entity.Affiliate;
import com.healthtourism.affiliateservice.entity.Referral;
import com.healthtourism.affiliateservice.repository.AffiliateRepository;
import com.healthtourism.affiliateservice.repository.ReferralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class AffiliateService {
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private ReferralRepository referralRepository;
    
    public Affiliate registerAffiliate(Long userId) {
        Affiliate affiliate = new Affiliate();
        affiliate.setUserId(userId);
        affiliate.setReferralCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        affiliate.setReferralLink("https://healthtourism.com/ref/" + affiliate.getReferralCode());
        return affiliateRepository.save(affiliate);
    }
    
    public Affiliate getAffiliateByCode(String referralCode) {
        return affiliateRepository.findByReferralCode(referralCode)
            .orElseThrow(() -> new RuntimeException("Affiliate bulunamadı"));
    }
    
    public Referral trackClick(String referralCode, Long userId) {
        Affiliate affiliate = getAffiliateByCode(referralCode);
        affiliate.setTotalClicks(affiliate.getTotalClicks() + 1);
        affiliateRepository.save(affiliate);
        
        Referral referral = new Referral();
        referral.setAffiliateId(affiliate.getId());
        referral.setReferredUserId(userId);
        referral.setStatus("CLICKED");
        return referralRepository.save(referral);
    }
    
    public Referral trackConversion(Long referralId, Long reservationId, Double amount) {
        Referral referral = referralRepository.findById(referralId)
            .orElseThrow(() -> new RuntimeException("Referral bulunamadı"));
        
        referral.setReservationId(reservationId);
        referral.setAmount(amount);
        
        Affiliate affiliate = affiliateRepository.findById(referral.getAffiliateId())
            .orElseThrow(() -> new RuntimeException("Affiliate bulunamadı"));
        
        Double commission = amount * (affiliate.getCommissionRate() / 100);
        referral.setCommission(commission);
        referral.setStatus("CONVERTED");
        
        affiliate.setTotalConversions(affiliate.getTotalConversions() + 1);
        affiliate.setTotalEarnings(affiliate.getTotalEarnings() + commission);
        affiliateRepository.save(affiliate);
        
        return referralRepository.save(referral);
    }
    
    public List<Referral> getReferralsByAffiliate(Long affiliateId) {
        return referralRepository.findByAffiliateId(affiliateId);
    }
}

