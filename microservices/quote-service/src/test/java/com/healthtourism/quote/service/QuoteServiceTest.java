package com.healthtourism.quote.service;

import com.healthtourism.quote.entity.Quote;
import com.healthtourism.quote.repository.QuoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private QuoteStateMachineService stateMachineService;

    @InjectMocks
    private QuoteService quoteService;

    private Quote quote;

    @BeforeEach
    void setUp() {
        quote = new Quote();
        quote.setId(1L);
        quote.setUserId(1L);
        quote.setHospitalId(1L);
        quote.setDoctorId(1L);
        quote.setTreatmentId(1L);
        quote.setStatus(Quote.QuoteStatus.DRAFT);
        quote.setTotalPrice(new BigDecimal("10000.00"));
        quote.setCurrency("TRY");
        quote.setValidUntil(LocalDateTime.now().plusDays(30));
    }

    @Test
    void testCreateQuote() {
        // Given
        when(quoteRepository.save(any(Quote.class))).thenReturn(quote);

        // When
        Quote result = quoteService.createQuote(quote);

        // Then
        assertNotNull(result);
        assertNotNull(result.getQuoteNumber());
        assertEquals(Quote.QuoteStatus.DRAFT, result.getStatus());
        verify(quoteRepository, times(1)).save(any(Quote.class));
    }

    @Test
    void testSubmitQuote() {
        // Given
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(quote));
        when(quoteRepository.save(any(Quote.class))).thenReturn(quote);

        // When
        Quote result = quoteService.submitQuote(1L);

        // Then
        assertNotNull(result);
        assertEquals(Quote.QuoteStatus.PENDING, result.getStatus());
        verify(quoteRepository, times(1)).save(any(Quote.class));
    }

    @Test
    void testAcceptQuote() {
        // Given
        quote.setStatus(Quote.QuoteStatus.SENT);
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(quote));
        when(quoteRepository.save(any(Quote.class))).thenReturn(quote);

        // When
        Quote result = quoteService.acceptQuote(1L);

        // Then
        assertNotNull(result);
        assertEquals(Quote.QuoteStatus.ACCEPTED, result.getStatus());
        verify(quoteRepository, times(1)).save(any(Quote.class));
    }

    @Test
    void testRejectQuote() {
        // Given
        quote.setStatus(Quote.QuoteStatus.SENT);
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(quote));
        when(quoteRepository.save(any(Quote.class))).thenReturn(quote);

        // When
        Quote result = quoteService.rejectQuote(1L, "Too expensive");

        // Then
        assertNotNull(result);
        assertEquals(Quote.QuoteStatus.REJECTED, result.getStatus());
        assertEquals("Too expensive", result.getRejectionReason());
        verify(quoteRepository, times(1)).save(any(Quote.class));
    }
}
