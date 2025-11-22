package com.healthtourism.testimonialservice.service;
import com.healthtourism.testimonialservice.dto.TestimonialDTO;
import com.healthtourism.testimonialservice.entity.Testimonial;
import com.healthtourism.testimonialservice.repository.TestimonialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestimonialService {
    @Autowired
    private TestimonialRepository testimonialRepository;
    
    public List<TestimonialDTO> getAllApprovedTestimonials() {
        return testimonialRepository.findAllApprovedOrderByCreatedAtDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TestimonialDTO> getTestimonialsByHospital(Long hospitalId) {
        return testimonialRepository.findByHospitalIdAndIsApprovedTrue(hospitalId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<TestimonialDTO> getTestimonialsByDoctor(Long doctorId) {
        return testimonialRepository.findByDoctorIdAndIsApprovedTrue(doctorId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public TestimonialDTO getTestimonialById(Long id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hasta hikayesi bulunamadı"));
        return convertToDTO(testimonial);
    }
    
    public TestimonialDTO createTestimonial(Testimonial testimonial) {
        testimonial.setIsApproved(false);
        testimonial.setCreatedAt(LocalDateTime.now());
        return convertToDTO(testimonialRepository.save(testimonial));
    }
    
    @Transactional
    public TestimonialDTO approveTestimonial(Long id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hasta hikayesi bulunamadı"));
        testimonial.setIsApproved(true);
        return convertToDTO(testimonialRepository.save(testimonial));
    }
    
    private TestimonialDTO convertToDTO(Testimonial testimonial) {
        TestimonialDTO dto = new TestimonialDTO();
        dto.setId(testimonial.getId());
        dto.setUserId(testimonial.getUserId());
        dto.setPatientName(testimonial.getPatientName());
        dto.setCountry(testimonial.getCountry());
        dto.setHospitalId(testimonial.getHospitalId());
        dto.setDoctorId(testimonial.getDoctorId());
        dto.setTestimonial(testimonial.getTestimonial());
        dto.setRating(testimonial.getRating());
        dto.setVideoUrl(testimonial.getVideoUrl());
        dto.setImageUrl(testimonial.getImageUrl());
        dto.setIsApproved(testimonial.getIsApproved());
        dto.setCreatedAt(testimonial.getCreatedAt());
        return dto;
    }
}

