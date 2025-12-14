package com.healthtourism.installmentservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class InstallmentService {
    public List<Map<String, Object>> getPlans(Double amount) {
        List<Map<String, Object>> plans = new ArrayList<>();
        for (int i = 3; i <= 12; i += 3) {
            Map<String, Object> plan = new HashMap<>();
            plan.put("installments", i);
            plan.put("monthlyAmount", amount / i);
            plan.put("totalAmount", amount);
            plan.put("interestRate", 0.0);
            plans.add(plan);
        }
        return plans;
    }

    public Map<String, Object> calculate(Double amount, Integer installments) {
        Map<String, Object> result = new HashMap<>();
        result.put("totalAmount", amount);
        result.put("installments", installments);
        result.put("monthlyAmount", amount / installments);
        result.put("interestRate", 0.0);
        return result;
    }
}

