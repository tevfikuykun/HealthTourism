package com.healthtourism.flightservice.repository;
import com.healthtourism.flightservice.entity.FlightBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightBookingRepository extends JpaRepository<FlightBooking, Long> {
    List<FlightBooking> findByIsAvailableTrue();
    List<FlightBooking> findByDepartureCityAndArrivalCityAndIsAvailableTrue(String departureCity, String arrivalCity);
    List<FlightBooking> findByFlightClassAndIsAvailableTrue(String flightClass);
    @Query("SELECT f FROM FlightBooking f WHERE f.isAvailable = true AND f.price <= :maxPrice ORDER BY f.price ASC")
    List<FlightBooking> findByPriceLessThanEqual(@Param("maxPrice") BigDecimal maxPrice);
    @Query("SELECT f FROM FlightBooking f WHERE f.isAvailable = true AND f.departureDateTime >= :date ORDER BY f.departureDateTime ASC")
    List<FlightBooking> findAvailableFlightsAfterDate(@Param("date") LocalDateTime date);
    Optional<FlightBooking> findByIdAndIsAvailableTrue(Long id);
}

