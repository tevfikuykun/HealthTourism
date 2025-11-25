package com.healthtourism.currencyconversionservice.service;

import com.healthtourism.currencyconversionservice.dto.CurrencyConversionRequest;
import com.healthtourism.currencyconversionservice.dto.CurrencyConversionResponse;
import com.healthtourism.currencyconversionservice.entity.ExchangeRate;
import com.healthtourism.currencyconversionservice.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CurrencyConversionService {
    
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;
    
    @Value("${currency.api.url:https://api.exchangerate-api.com/v4/latest}")
    private String exchangeRateApiUrl;
    
    @Value("${currency.base:TRY}")
    private String baseCurrency;
    
    private final WebClient webClient = WebClient.create();
    
    @Cacheable(value = "exchangeRates", key = "#fromCurrency + '-' + #toCurrency")
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        // Eğer aynı para birimi ise 1 döndür
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return BigDecimal.ONE;
        }
        
        // Önce cache/database'den kontrol et
        Optional<ExchangeRate> cachedRate = exchangeRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
        if (cachedRate.isPresent()) {
            ExchangeRate rate = cachedRate.get();
            // 1 saatten eski değilse cache'den döndür
            if (rate.getLastUpdated().isAfter(LocalDateTime.now().minusHours(1))) {
                return rate.getRate();
            }
        }
        
        // API'den güncel kur çek (örnek: exchangerate-api.com)
        BigDecimal rate = fetchExchangeRateFromApi(fromCurrency, toCurrency);
        
        // Database'e kaydet
        ExchangeRate exchangeRate = cachedRate.orElse(new ExchangeRate());
        exchangeRate.setFromCurrency(fromCurrency);
        exchangeRate.setToCurrency(toCurrency);
        exchangeRate.setRate(rate);
        exchangeRate.setLastUpdated(LocalDateTime.now());
        exchangeRateRepository.save(exchangeRate);
        
        return rate;
    }
    
    public CurrencyConversionResponse convertCurrency(CurrencyConversionRequest request) {
        BigDecimal rate = getExchangeRate(request.getFromCurrency(), request.getToCurrency());
        BigDecimal convertedAmount = request.getAmount().multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);
        
        CurrencyConversionResponse response = new CurrencyConversionResponse();
        response.setOriginalAmount(request.getAmount());
        response.setFromCurrency(request.getFromCurrency());
        response.setConvertedAmount(convertedAmount);
        response.setToCurrency(request.getToCurrency());
        response.setExchangeRate(rate);
        response.setConversionDate(LocalDateTime.now());
        
        return response;
    }
    
    private BigDecimal fetchExchangeRateFromApi(String fromCurrency, String toCurrency) {
        try {
            // ExchangeRate-API (ücretsiz) kullanımı
            // Gerçek uygulamada API key gerekebilir
            String url = exchangeRateApiUrl + "/" + fromCurrency;
            
            // Basit bir implementasyon - gerçek API response'una göre güncellenebilir
            // Şu an için manuel kur oranları (TRY bazlı)
            return getDefaultExchangeRate(fromCurrency, toCurrency);
            
        } catch (Exception e) {
            // Hata durumunda default kurları kullan
            return getDefaultExchangeRate(fromCurrency, toCurrency);
        }
    }
    
    private BigDecimal getDefaultExchangeRate(String fromCurrency, String toCurrency) {
        // Default kurlar (TRY bazlı - güncel değil, örnek amaçlı)
        if (fromCurrency.equalsIgnoreCase("TRY")) {
            switch (toCurrency.toUpperCase()) {
                case "USD": return new BigDecimal("0.034");
                case "EUR": return new BigDecimal("0.031");
                case "GBP": return new BigDecimal("0.027");
                default: return BigDecimal.ONE;
            }
        } else if (toCurrency.equalsIgnoreCase("TRY")) {
            // Ters çeviri
            BigDecimal rate = getDefaultExchangeRate("TRY", fromCurrency);
            return BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_UP);
        } else {
            // İki yabancı para birimi arasında (TRY üzerinden)
            BigDecimal fromRate = getDefaultExchangeRate("TRY", fromCurrency);
            BigDecimal toRate = getDefaultExchangeRate("TRY", toCurrency);
            return toRate.divide(fromRate, 6, RoundingMode.HALF_UP);
        }
    }
}

