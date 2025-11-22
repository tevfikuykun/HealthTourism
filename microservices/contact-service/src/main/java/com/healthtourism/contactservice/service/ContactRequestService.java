package com.healthtourism.contactservice.service;
import com.healthtourism.contactservice.dto.ContactRequestDTO;
import com.healthtourism.contactservice.entity.ContactRequest;
import com.healthtourism.contactservice.repository.ContactRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactRequestService {
    @Autowired
    private ContactRequestRepository contactRequestRepository;
    
    public List<ContactRequestDTO> getAllRequests() {
        return contactRequestRepository.findAllOrderByCreatedAtDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<ContactRequestDTO> getRequestsByStatus(String status) {
        return contactRequestRepository.findByStatus(status)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public ContactRequestDTO getRequestById(Long id) {
        ContactRequest request = contactRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İletişim talebi bulunamadı"));
        return convertToDTO(request);
    }
    
    public ContactRequestDTO createRequest(ContactRequest request) {
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());
        return convertToDTO(contactRequestRepository.save(request));
    }
    
    @Transactional
    public ContactRequestDTO respondToRequest(Long id, String response) {
        ContactRequest request = contactRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İletişim talebi bulunamadı"));
        request.setResponse(response);
        request.setStatus("RESPONDED");
        request.setRespondedAt(LocalDateTime.now());
        return convertToDTO(contactRequestRepository.save(request));
    }
    
    private ContactRequestDTO convertToDTO(ContactRequest request) {
        ContactRequestDTO dto = new ContactRequestDTO();
        dto.setId(request.getId());
        dto.setName(request.getName());
        dto.setEmail(request.getEmail());
        dto.setPhone(request.getPhone());
        dto.setSubject(request.getSubject());
        dto.setMessage(request.getMessage());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setRespondedAt(request.getRespondedAt());
        dto.setResponse(request.getResponse());
        return dto;
    }
}

