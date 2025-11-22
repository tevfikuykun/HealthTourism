package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.DoctorDTO;
import com.example.HealthTourism.entity.Doctor;
import com.example.HealthTourism.repository.DoctorRepository;
import com.example.HealthTourism.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    public List<DoctorDTO> getDoctorsByHospital(Long hospitalId) {
        return doctorRepository.findByHospitalIdAndIsAvailableTrue(hospitalId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationOrderByRatingDesc(specialization)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findByIdAndIsAvailableTrue(id)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));
        return convertToDTO(doctor);
    }
    
    public List<DoctorDTO> getTopRatedDoctorsByHospital(Long hospitalId) {
        return doctorRepository.findByHospitalIdOrderByRatingDesc(hospitalId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public DoctorDTO createDoctor(Doctor doctor) {
        doctor.setIsAvailable(true);
        doctor.setRating(0.0);
        doctor.setTotalReviews(0);
        Doctor saved = doctorRepository.save(doctor);
        return convertToDTO(saved);
    }
    
    private DoctorDTO convertToDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setFullName(doctor.getTitle() + " " + doctor.getFirstName() + " " + doctor.getLastName());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setTitle(doctor.getTitle());
        dto.setBio(doctor.getBio());
        dto.setExperienceYears(doctor.getExperienceYears());
        dto.setLanguages(doctor.getLanguages());
        dto.setRating(doctor.getRating());
        dto.setTotalReviews(doctor.getTotalReviews());
        dto.setConsultationFee(doctor.getConsultationFee());
        dto.setIsAvailable(doctor.getIsAvailable());
        dto.setHospitalId(doctor.getHospital().getId());
        dto.setHospitalName(doctor.getHospital().getName());
        return dto;
    }
}

