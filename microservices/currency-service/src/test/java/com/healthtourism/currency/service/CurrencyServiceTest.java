package com.healthtourism.currency.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(currencyService, "exchangeRateApiUrl", "https://api.exchangerate-api.com/v4/latest/TRY");
    }

    @Test
    void testConvertCurrency_SameCurrency() {
        // When
        BigDecimal result = currencyService.convertCurrency(
            new BigDecimal("1000.00"), "TRY", "TRY"
        );

        // Then
        assertEquals(new BigDecimal("1000.00"), result);
    }

    @Test
    void testConvertCurrency_DifferentCurrency() {
        // When
        BigDecimal result = currencyService.convertCurrency(
            new BigDecimal("1000.00"), "TRY", "USD"
        );

        // Then
        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testGetExchangeRate() {
        // When
        BigDecimal rate = currencyService.getExchangeRate("TRY", "USD");

        // Then
        assertNotNull(rate);
        assertTrue(rate.compareTo(BigDecimal.ZERO) > 0);
    }
}
