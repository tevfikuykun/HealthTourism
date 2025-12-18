package com.healthtourism.quote.repository;

import com.healthtourism.quote.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Quote> findByStatus(Quote.QuoteStatus status);
    List<Quote> findByStatusAndValidUntilAfter(Quote.QuoteStatus status, LocalDateTime date);
    Optional<Quote> findByQuoteNumber(String quoteNumber);
    List<Quote> findByUserIdAndStatus(Long userId, Quote.QuoteStatus status);
}
