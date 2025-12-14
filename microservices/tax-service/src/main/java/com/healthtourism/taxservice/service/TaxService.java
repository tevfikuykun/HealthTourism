package com.healthtourism.taxservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TaxService {
    public Map<String, Object> calculate(Double amount, Double taxRate, Boolean includeTax) {
        Map<String, Object> result = new HashMap<>();
        if (includeTax != null && includeTax) {
            Double taxAmount = amount * (taxRate / 100);
            result.put("amount", amount);
            result.put("taxRate", taxRate);
            result.put("taxAmount", taxAmount);
            result.put("total", amount + taxAmount);
        } else {
            Double taxAmount = amount * (taxRate / 100);
            result.put("amount", amount);
            result.put("taxRate", taxRate);
            result.put("taxAmount", taxAmount);
            result.put("total", amount);
        }
        return result;
    }
}

