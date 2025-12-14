package com.healthtourism.referralservice.service;

import com.healthtourism.referralservice.entity.Referral;
import com.healthtourism.referralservice.repository.ReferralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReferralService {
    @Autowired
    private ReferralRepository referralRepository;

    public String getCode(Long userId) {
        Optional<Referral> existing = referralRepository.findByUserId(userId);
        if (existing.isPresent()) {
            return existing.get().getCode();
        }
        Referral referral = new Referral();
        referral.setUserId(userId);
        referral.setCode("REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        referral = referralRepository.save(referral);
        return referral.getCode();
    }

    public Map<String, Object> useCode(String code) {
        Optional<Referral> referral = referralRepository.findByCode(code);
        if (referral.isEmpty()) {
            throw new RuntimeException("Geçersiz referans kodu");
        }
        Referral ref = referral.get();
        ref.setUsedCount(ref.getUsedCount() + 1);
        referralRepository.save(ref);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("userId", ref.getUserId());
        return result;
    }

    public Map<String, Object> getStats(Long userId) {
        Optional<Referral> referral = referralRepository.findByUserId(userId);
        Map<String, Object> stats = new HashMap<>();
        if (referral.isPresent()) {
            Referral ref = referral.get();
            stats.put("code", ref.getCode());
            stats.put("usedCount", ref.getUsedCount());
            stats.put("totalEarnings", ref.getTotalEarnings());
        } else {
            stats.put("code", "");
            stats.put("usedCount", 0);
            stats.put("totalEarnings", 0);
        }
        return stats;
    }
}

