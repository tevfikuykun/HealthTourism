package com.healthtourism.eventsourcing.repository;

import com.healthtourism.eventsourcing.model.ReservationReadModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationReadModelRepository extends MongoRepository<ReservationReadModel, String> {
    Optional<ReservationReadModel> findByReservationId(String reservationId);
    List<ReservationReadModel> findByUserId(Long userId);
    List<ReservationReadModel> findByHospitalId(Long hospitalId);
    List<ReservationReadModel> findByStatus(String status);
}

