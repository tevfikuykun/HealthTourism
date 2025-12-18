package com.healthtourism.pricecalculation.service;

import com.healthtourism.pricecalculation.dto.PriceCalculationRequest;
import com.healthtourism.pricecalculation.dto.PriceCalculationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PriceCalculationServiceTest {

    @InjectMocks
    private PriceCalculationService priceCalculationService;

    private PriceCalculationRequest request;

    @BeforeEach
    void setUp() {
        request = new PriceCalculationRequest();
        request.setHospitalPrice(new BigDecimal("5000.00"));
        request.setDoctorPrice(new BigDecimal("2000.00"));
        request.setTreatmentPrice(new BigDecimal("3000.00"));
        request.setFlightPrice(new BigDecimal("1500.00"));
        request.setAccommodationPricePerNight(new BigDecimal("200.00"));
        request.setAccommodationNights(5);
        request.setCurrency("TRY");
    }

    @Test
    void testCalculatePrice_BaseCalculation() {
        // When
        PriceCalculationResponse response = priceCalculationService.calculatePrice(request);

        // Then
        assertNotNull(response);
        assertEquals(new BigDecimal("10000.00"), response.getBasePrice());
        assertEquals(new BigDecimal("1500.00"), response.getFlightPrice());
        assertEquals(new BigDecimal("1000.00"), response.getAccommodationPrice());
        assertTrue(response.getTotalPrice().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCalculatePrice_WithAdditionalServices() {
        // Given
        PriceCalculationRequest.AdditionalService translator = new PriceCalculationRequest.AdditionalService("TRANSLATOR", new BigDecimal("300.00"), 1);
        PriceCalculationRequest.AdditionalService vipTransfer = new PriceCalculationRequest.AdditionalService("VIP_TRANSFER", new BigDecimal("500.00"), 1);
        request.setAdditionalServices(Arrays.asList(translator, vipTransfer));

        // When
        PriceCalculationResponse response = priceCalculationService.calculatePrice(request);

        // Then
        assertNotNull(response);
        assertEquals(new BigDecimal("800.00"), response.getAdditionalServicesPrice());
        assertTrue(response.getTotalPrice().compareTo(new BigDecimal("10000.00")) > 0);
    }

    @Test
    void testCalculatePrice_WithDiscount() {
        // Given - Total > 10000 TRY
        request.setHospitalPrice(new BigDecimal("15000.00"));

        // When
        PriceCalculationResponse response = priceCalculationService.calculatePrice(request);

        // Then
        assertNotNull(response);
        assertTrue(response.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCalculatePriceWithDiscount() {
        // When
        PriceCalculationResponse response = priceCalculationService.calculatePriceWithDiscount(request, new BigDecimal("10"));

        // Then
        assertNotNull(response);
        assertTrue(response.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0);
    }
}
