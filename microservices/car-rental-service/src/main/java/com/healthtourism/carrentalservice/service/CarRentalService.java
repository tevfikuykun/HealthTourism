package com.healthtourism.carrentalservice.service;
import com.healthtourism.carrentalservice.dto.CarRentalDTO;
import com.healthtourism.carrentalservice.entity.CarRental;
import com.healthtourism.carrentalservice.repository.CarRentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarRentalService {
    @Autowired
    private CarRentalRepository carRentalRepository;
    
    public List<CarRentalDTO> getAllAvailableCarRentals() {
        return carRentalRepository.findByIsAvailableTrue()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<CarRentalDTO> getCarRentalsByType(String carType) {
        return carRentalRepository.findByCarTypeAndIsAvailableTrue(carType)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<CarRentalDTO> getCarRentalsByPrice(BigDecimal maxPrice) {
        return carRentalRepository.findByPricePerDayLessThanEqual(maxPrice)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public CarRentalDTO getCarRentalById(Long id) {
        CarRental carRental = carRentalRepository.findByIdAndIsAvailableTrue(id)
                .orElseThrow(() -> new com.healthtourism.carrentalservice.exception.ResourceNotFoundException("Araç kiralama bulunamadı: " + id));
        return convertToDTO(carRental);
    }
    
    private CarRentalDTO convertToDTO(CarRental carRental) {
        CarRentalDTO dto = new CarRentalDTO();
        dto.setId(carRental.getId());
        dto.setCompanyName(carRental.getCompanyName());
        dto.setCarModel(carRental.getCarModel());
        dto.setCarType(carRental.getCarType());
        dto.setPassengerCapacity(carRental.getPassengerCapacity());
        dto.setLuggageCapacity(carRental.getLuggageCapacity());
        dto.setTransmission(carRental.getTransmission());
        dto.setHasAirConditioning(carRental.getHasAirConditioning());
        dto.setHasGPS(carRental.getHasGPS());
        dto.setPricePerDay(carRental.getPricePerDay());
        dto.setPricePerWeek(carRental.getPricePerWeek());
        dto.setPricePerMonth(carRental.getPricePerMonth());
        dto.setPickupLocation(carRental.getPickupLocation());
        dto.setDropoffLocation(carRental.getDropoffLocation());
        dto.setPhone(carRental.getPhone());
        dto.setEmail(carRental.getEmail());
        dto.setDescription(carRental.getDescription());
        dto.setRating(carRental.getRating());
        dto.setTotalReviews(carRental.getTotalReviews());
        dto.setIsAvailable(carRental.getIsAvailable());
        dto.setIncludesInsurance(carRental.getIncludesInsurance());
        dto.setIncludesDriver(carRental.getIncludesDriver());
        return dto;
    }
}

