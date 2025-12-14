package com.healthtourism.currencyservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CurrencyService {
    public Map<String, Double> getRates() {
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.92);
        rates.put("TRY", 32.5);
        rates.put("GBP", 0.79);
        return rates;
    }

    public Map<String, Object> convert(String from, String to, Double amount) {
        Map<String, Double> rates = getRates();
        Double fromRate = rates.getOrDefault(from, 1.0);
        Double toRate = rates.getOrDefault(to, 1.0);
        Double converted = (amount / fromRate) * toRate;
        return Map.of("from", from, "to", to, "amount", amount, "converted", converted);
    }
}

