package com.healthtourism.reservationservice.repository;

import com.healthtourism.reservationservice.entity.ReservationEventStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationEventStoreRepository extends JpaRepository<ReservationEventStore, Long> {
    List<ReservationEventStore> findByReservationIdOrderByEventTimestampAsc(Long reservationId);
    List<ReservationEventStore> findByEventTypeOrderByEventTimestampDesc(String eventType);
}


