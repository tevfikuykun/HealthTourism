package com.healthtourism.quote.service;

import com.healthtourism.quote.entity.Quote;
import com.healthtourism.quote.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuoteService {
    
    @Autowired
    private QuoteRepository quoteRepository;
    
    @Autowired
    private QuoteStateMachineService stateMachineService;
    
    @Transactional
    public Quote createQuote(Quote quote) {
        quote.setQuoteNumber("QUOTE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        quote.setStatus(Quote.QuoteStatus.DRAFT);
        quote.setCreatedAt(LocalDateTime.now());
        quote.setValidUntil(LocalDateTime.now().plusDays(30)); // 30 days validity
        return quoteRepository.save(quote);
    }
    
    @Transactional
    public Quote submitQuote(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
            .orElseThrow(() -> new RuntimeException("Teklif bulunamadı"));
        
        if (quote.getStatus() != Quote.QuoteStatus.DRAFT) {
            throw new RuntimeException("Sadece taslak teklifler gönderilebilir");
        }
        
        quote.setStatus(Quote.QuoteStatus.PENDING);
        quote.setUpdatedAt(LocalDateTime.now());
        return quoteRepository.save(quote);
    }
    
    @Transactional
    public Quote sendToPatient(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
            .orElseThrow(() -> new RuntimeException("Teklif bulunamadı"));
        
        if (quote.getStatus() != Quote.QuoteStatus.PENDING) {
            throw new RuntimeException("Sadece bekleyen teklifler hastaya gönderilebilir");
        }
        
        quote.setStatus(Quote.QuoteStatus.SENT);
        quote.setUpdatedAt(LocalDateTime.now());
        Quote savedQuote = quoteRepository.save(quote);
        
        // Trigger reminder creation (async call to reminder service)
        try {
            createReminderForQuote(savedQuote);
        } catch (Exception e) {
            // Log error but don't fail the quote sending
            System.err.println("Failed to create reminder: " + e.getMessage());
        }
        
        return savedQuote;
    }
    
    private void createReminderForQuote(Quote quote) {
        // Call reminder service to create automatic reminder
        // This will be called asynchronously in production
        // For now, reminder service will automatically detect new quotes via scheduled job
        // This method is a placeholder for future direct integration
    }
    
    @Transactional
    public Quote acceptQuote(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
            .orElseThrow(() -> new RuntimeException("Teklif bulunamadı"));
        
        if (quote.getStatus() != Quote.QuoteStatus.SENT) {
            throw new RuntimeException("Sadece gönderilmiş teklifler kabul edilebilir");
        }
        
        if (LocalDateTime.now().isAfter(quote.getValidUntil())) {
            quote.setStatus(Quote.QuoteStatus.EXPIRED);
            quoteRepository.save(quote);
            throw new RuntimeException("Teklif süresi dolmuş");
        }
        
        quote.setStatus(Quote.QuoteStatus.ACCEPTED);
        quote.setApprovedAt(LocalDateTime.now());
        quote.setUpdatedAt(LocalDateTime.now());
        return quoteRepository.save(quote);
    }
    
    @Transactional
    public Quote rejectQuote(Long quoteId, String reason) {
        Quote quote = quoteRepository.findById(quoteId)
            .orElseThrow(() -> new RuntimeException("Teklif bulunamadı"));
        
        if (quote.getStatus() != Quote.QuoteStatus.SENT) {
            throw new RuntimeException("Sadece gönderilmiş teklifler reddedilebilir");
        }
        
        quote.setStatus(Quote.QuoteStatus.REJECTED);
        quote.setRejectionReason(reason);
        quote.setRejectedAt(LocalDateTime.now());
        quote.setUpdatedAt(LocalDateTime.now());
        return quoteRepository.save(quote);
    }
    
    @Transactional
    public Quote convertToReservation(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
            .orElseThrow(() -> new RuntimeException("Teklif bulunamadı"));
        
        if (quote.getStatus() != Quote.QuoteStatus.ACCEPTED) {
            throw new RuntimeException("Sadece kabul edilmiş teklifler rezervasyona dönüştürülebilir");
        }
        
        quote.setStatus(Quote.QuoteStatus.CONVERTED);
        quote.setUpdatedAt(LocalDateTime.now());
        return quoteRepository.save(quote);
    }
    
    public List<Quote> getQuotesByUser(Long userId) {
        return quoteRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<Quote> getQuotesByStatus(Quote.QuoteStatus status) {
        return quoteRepository.findByStatus(status);
    }
    
    public Quote getQuoteById(Long id) {
        return quoteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Teklif bulunamadı"));
    }
    
    @Transactional
    public void expireOldQuotes() {
        List<Quote> expiredQuotes = quoteRepository.findByStatusAndValidUntilAfter(
            Quote.QuoteStatus.SENT, LocalDateTime.now()
        );
        
        expiredQuotes.forEach(quote -> {
            if (LocalDateTime.now().isAfter(quote.getValidUntil())) {
                quote.setStatus(Quote.QuoteStatus.EXPIRED);
                quote.setUpdatedAt(LocalDateTime.now());
                quoteRepository.save(quote);
            }
        });
    }
}
