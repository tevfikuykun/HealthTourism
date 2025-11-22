package com.healthtourism.appointmentcalendarservice.service;
import com.healthtourism.appointmentcalendarservice.dto.AppointmentSlotDTO;
import com.healthtourism.appointmentcalendarservice.entity.AppointmentSlot;
import com.healthtourism.appointmentcalendarservice.repository.AppointmentSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentSlotService {
    @Autowired
    private AppointmentSlotRepository appointmentSlotRepository;
    
    public List<AppointmentSlotDTO> getAvailableSlotsByDoctor(Long doctorId) {
        return appointmentSlotRepository.findByDoctorIdAndIsAvailableTrue(doctorId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<AppointmentSlotDTO> getSlotsByDoctorAndDateRange(Long doctorId, LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentSlotRepository.findByDoctorIdAndDateRange(doctorId, startDate, endDate)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public AppointmentSlotDTO createSlot(AppointmentSlot slot) {
        slot.setIsAvailable(true);
        slot.setCreatedAt(LocalDateTime.now());
        return convertToDTO(appointmentSlotRepository.save(slot));
    }
    
    private AppointmentSlotDTO convertToDTO(AppointmentSlot slot) {
        AppointmentSlotDTO dto = new AppointmentSlotDTO();
        dto.setId(slot.getId());
        dto.setDoctorId(slot.getDoctorId());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setIsAvailable(slot.getIsAvailable());
        dto.setCreatedAt(slot.getCreatedAt());
        return dto;
    }
}

