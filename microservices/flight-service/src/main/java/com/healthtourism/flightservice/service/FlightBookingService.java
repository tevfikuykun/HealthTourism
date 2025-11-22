package com.healthtourism.flightservice.service;
import com.healthtourism.flightservice.dto.FlightBookingDTO;
import com.healthtourism.flightservice.entity.FlightBooking;
import com.healthtourism.flightservice.repository.FlightBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightBookingService {
    @Autowired
    private FlightBookingRepository flightBookingRepository;
    
    public List<FlightBookingDTO> getAllAvailableFlights() {
        return flightBookingRepository.findByIsAvailableTrue()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<FlightBookingDTO> searchFlights(String departureCity, String arrivalCity) {
        return flightBookingRepository.findByDepartureCityAndArrivalCityAndIsAvailableTrue(departureCity, arrivalCity)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<FlightBookingDTO> getFlightsByClass(String flightClass) {
        return flightBookingRepository.findByFlightClassAndIsAvailableTrue(flightClass)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<FlightBookingDTO> getFlightsByPrice(BigDecimal maxPrice) {
        return flightBookingRepository.findByPriceLessThanEqual(maxPrice)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public FlightBookingDTO getFlightById(Long id) {
        FlightBooking flight = flightBookingRepository.findByIdAndIsAvailableTrue(id)
                .orElseThrow(() -> new RuntimeException("Uçuş bulunamadı"));
        return convertToDTO(flight);
    }
    
    private FlightBookingDTO convertToDTO(FlightBooking flight) {
        FlightBookingDTO dto = new FlightBookingDTO();
        dto.setId(flight.getId());
        dto.setAirlineName(flight.getAirlineName());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setDepartureCity(flight.getDepartureCity());
        dto.setArrivalCity(flight.getArrivalCity());
        dto.setDepartureDateTime(flight.getDepartureDateTime());
        dto.setArrivalDateTime(flight.getArrivalDateTime());
        dto.setFlightClass(flight.getFlightClass());
        dto.setAvailableSeats(flight.getAvailableSeats());
        dto.setPrice(flight.getPrice());
        dto.setDurationMinutes(flight.getDurationMinutes());
        dto.setHasMeal(flight.getHasMeal());
        dto.setHasEntertainment(flight.getHasEntertainment());
        dto.setBaggageAllowance(flight.getBaggageAllowance());
        dto.setIsDirectFlight(flight.getIsDirectFlight());
        dto.setDescription(flight.getDescription());
        dto.setRating(flight.getRating());
        dto.setTotalReviews(flight.getTotalReviews());
        dto.setIsAvailable(flight.getIsAvailable());
        return dto;
    }
}

