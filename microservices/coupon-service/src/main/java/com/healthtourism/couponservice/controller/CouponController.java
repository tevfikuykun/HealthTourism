package com.healthtourism.couponservice.controller;
import com.healthtourism.couponservice.entity.Coupon;
import com.healthtourism.couponservice.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "*")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @GetMapping
    public ResponseEntity<List<Coupon>> getAll() {
        return ResponseEntity.ok(couponService.getAll());
    }

    @GetMapping("/{code}")
    public ResponseEntity<Coupon> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(couponService.getByCode(code));
    }

    @PostMapping("/redeem")
    public ResponseEntity<Map<String, Object>> redeem(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(couponService.redeem(request.get("code")));
    }
}

