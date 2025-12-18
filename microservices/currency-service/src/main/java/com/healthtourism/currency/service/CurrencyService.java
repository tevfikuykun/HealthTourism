package com.healthtourism.currency.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Currency Conversion Service
 * Supports multiple currencies (TRY, USD, EUR, GBP, etc.)
 */
@Service
public class CurrencyService {

    @Value("${currency.api.url:https://api.exchangerate-api.com/v4/latest/TRY}")
    private String exchangeRateApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Fallback exchange rates (if API is unavailable)
    private static final Map<String, BigDecimal> FALLBACK_RATES = new HashMap<>();
    static {
        FALLBACK_RATES.put("USD", new BigDecimal("0.033"));
        FALLBACK_RATES.put("EUR", new BigDecimal("0.030"));
        FALLBACK_RATES.put("GBP", new BigDecimal("0.026"));
        FALLBACK_RATES.put("TRY", BigDecimal.ONE);
    }

    @Cacheable(value = "exchangeRates", key = "#baseCurrency")
    public Map<String, BigDecimal> getExchangeRates(String baseCurrency) {
        try {
            // In production, call external API
            // For now, return fallback rates
            return getFallbackRates(baseCurrency);
        } catch (Exception e) {
            return getFallbackRates(baseCurrency);
        }
    }

    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        Map<String, BigDecimal> rates = getExchangeRates("TRY");
        
        // Convert to TRY first, then to target currency
        BigDecimal amountInTRY;
        if (fromCurrency.equals("TRY")) {
            amountInTRY = amount;
        } else {
            BigDecimal fromRate = rates.getOrDefault(fromCurrency, BigDecimal.ONE);
            amountInTRY = amount.divide(fromRate, 2, RoundingMode.HALF_UP);
        }

        // Convert from TRY to target currency
        if (toCurrency.equals("TRY")) {
            return amountInTRY;
        } else {
            BigDecimal toRate = rates.getOrDefault(toCurrency, BigDecimal.ONE);
            return amountInTRY.multiply(toRate).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }

        Map<String, BigDecimal> rates = getExchangeRates("TRY");
        BigDecimal fromRate = rates.getOrDefault(fromCurrency, BigDecimal.ONE);
        BigDecimal toRate = rates.getOrDefault(toCurrency, BigDecimal.ONE);
        
        return toRate.divide(fromRate, 4, RoundingMode.HALF_UP);
    }

    private Map<String, BigDecimal> getFallbackRates(String baseCurrency) {
        // Return rates relative to base currency
        Map<String, BigDecimal> rates = new HashMap<>();
        BigDecimal baseRate = FALLBACK_RATES.getOrDefault(baseCurrency, BigDecimal.ONE);
        
        FALLBACK_RATES.forEach((currency, rate) -> {
            if (!currency.equals(baseCurrency)) {
                rates.put(currency, rate.divide(baseRate, 4, RoundingMode.HALF_UP));
            } else {
                rates.put(currency, BigDecimal.ONE);
            }
        });
        
        return rates;
    }
}
