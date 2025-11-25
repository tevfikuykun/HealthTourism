package com.healthtourism.paymentservice.repository;

import com.healthtourism.paymentservice.entity.PaymentEventStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentEventStoreRepository extends JpaRepository<PaymentEventStore, Long> {
    List<PaymentEventStore> findByPaymentIdOrderByEventTimestampAsc(Long paymentId);
    List<PaymentEventStore> findByEventTypeOrderByEventTimestampDesc(String eventType);
}


