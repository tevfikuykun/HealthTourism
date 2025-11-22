package com.healthtourism.transferservice.service;
import com.healthtourism.transferservice.dto.TransferServiceDTO;
import com.healthtourism.transferservice.entity.TransferService;
import com.healthtourism.transferservice.repository.TransferServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferServiceService {
    @Autowired
    private TransferServiceRepository transferServiceRepository;
    
    public List<TransferServiceDTO> getAllAvailableTransfers() {
        return transferServiceRepository.findByIsAvailableTrue()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TransferServiceDTO> getTransfersByType(String serviceType) {
        return transferServiceRepository.findByServiceTypeAndIsAvailableTrue(serviceType)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TransferServiceDTO> getTransfersByPrice(BigDecimal maxPrice) {
        return transferServiceRepository.findByPriceLessThanEqual(maxPrice)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public TransferServiceDTO getTransferById(Long id) {
        TransferService transfer = transferServiceRepository.findByIdAndIsAvailableTrue(id)
                .orElseThrow(() -> new RuntimeException("Transfer hizmeti bulunamadı"));
        return convertToDTO(transfer);
    }
    
    private TransferServiceDTO convertToDTO(TransferService transfer) {
        TransferServiceDTO dto = new TransferServiceDTO();
        dto.setId(transfer.getId());
        dto.setCompanyName(transfer.getCompanyName());
        dto.setVehicleType(transfer.getVehicleType());
        dto.setPassengerCapacity(transfer.getPassengerCapacity());
        dto.setServiceType(transfer.getServiceType());
        dto.setPickupLocation(transfer.getPickupLocation());
        dto.setDropoffLocation(transfer.getDropoffLocation());
        dto.setPrice(transfer.getPrice());
        dto.setDurationMinutes(transfer.getDurationMinutes());
        dto.setDistanceKm(transfer.getDistanceKm());
        dto.setHasMeetAndGreet(transfer.getHasMeetAndGreet());
        dto.setHasLuggageAssistance(transfer.getHasLuggageAssistance());
        dto.setHasWifi(transfer.getHasWifi());
        dto.setHasChildSeat(transfer.getHasChildSeat());
        dto.setPhone(transfer.getPhone());
        dto.setEmail(transfer.getEmail());
        dto.setDescription(transfer.getDescription());
        dto.setRating(transfer.getRating());
        dto.setTotalReviews(transfer.getTotalReviews());
        dto.setIsAvailable(transfer.getIsAvailable());
        return dto;
    }
}

