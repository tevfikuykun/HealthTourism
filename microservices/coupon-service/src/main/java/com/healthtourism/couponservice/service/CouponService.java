package com.healthtourism.couponservice.service;
import com.healthtourism.couponservice.entity.Coupon;
import com.healthtourism.couponservice.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CouponService {
    @Autowired
    private CouponRepository couponRepository;

    public List<Coupon> getAll() {
        return couponRepository.findAll();
    }

    public Coupon getByCode(String code) {
        return couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Kupon bulunamadı"));
    }

    public Map<String, Object> redeem(String code) {
        Coupon coupon = getByCode(code);
        if (!coupon.getIsActive()) {
            throw new RuntimeException("Kupon aktif değil");
        }
        if (coupon.getValidTo() != null && coupon.getValidTo().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Kupon süresi dolmuş");
        }
        if (coupon.getMaxUses() != null && coupon.getUsedCount() >= coupon.getMaxUses()) {
            throw new RuntimeException("Kupon kullanım limiti dolmuş");
        }
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
        return Map.of("success", true, "discountAmount", coupon.getDiscountAmount() != null ? coupon.getDiscountAmount() : 0,
                "discountPercentage", coupon.getDiscountPercentage() != null ? coupon.getDiscountPercentage() : 0);
    }
}

