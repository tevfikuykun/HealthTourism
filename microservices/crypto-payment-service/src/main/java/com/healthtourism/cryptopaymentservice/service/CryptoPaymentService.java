package com.healthtourism.cryptopaymentservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CryptoPaymentService {
    public Map<String, Object> createPayment(Map<String, Object> payment) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", UUID.randomUUID().toString());
        result.put("status", "pending");
        result.put("cryptoAddress", "0x" + UUID.randomUUID().toString().replace("-", "").substring(0, 40));
        result.put("amount", payment.get("amount"));
        result.put("currency", payment.get("currency"));
        return result;
    }

    public Map<String, Object> getStatus(String id) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("status", "confirmed");
        return result;
    }
}

