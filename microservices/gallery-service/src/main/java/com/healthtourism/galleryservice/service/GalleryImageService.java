package com.healthtourism.galleryservice.service;
import com.healthtourism.galleryservice.dto.GalleryImageDTO;
import com.healthtourism.galleryservice.entity.GalleryImage;
import com.healthtourism.galleryservice.repository.GalleryImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GalleryImageService {
    @Autowired
    private GalleryImageRepository galleryImageRepository;
    
    public List<GalleryImageDTO> getImagesByType(String imageType) {
        return galleryImageRepository.findByImageTypeAndIsActiveTrue(imageType)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<GalleryImageDTO> getImagesByTypeAndRelatedId(String imageType, Long relatedId) {
        return galleryImageRepository.findByImageTypeAndRelatedIdOrderByDisplayOrder(imageType, relatedId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public GalleryImageDTO getImageById(Long id) {
        GalleryImage image = galleryImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Görsel bulunamadı"));
        return convertToDTO(image);
    }
    
    public GalleryImageDTO createImage(GalleryImage image) {
        image.setIsActive(true);
        image.setUploadedAt(LocalDateTime.now());
        return convertToDTO(galleryImageRepository.save(image));
    }
    
    private GalleryImageDTO convertToDTO(GalleryImage image) {
        GalleryImageDTO dto = new GalleryImageDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setImageType(image.getImageType());
        dto.setRelatedId(image.getRelatedId());
        dto.setTitle(image.getTitle());
        dto.setDescription(image.getDescription());
        dto.setDisplayOrder(image.getDisplayOrder());
        dto.setIsActive(image.getIsActive());
        dto.setUploadedAt(image.getUploadedAt());
        return dto;
    }
}

