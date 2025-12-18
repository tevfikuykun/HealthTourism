package com.healthtourism.virtualtourservice.service;

import com.healthtourism.virtualtourservice.entity.VirtualTour;
import com.healthtourism.virtualtourservice.repository.VirtualTourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VirtualTourService {
    
    @Autowired
    private VirtualTourRepository virtualTourRepository;
    
    public List<VirtualTour> getAllActiveTours() {
        return virtualTourRepository.findByStatusOrderByViewCountDesc("ACTIVE");
    }
    
    public List<VirtualTour> getToursByType(String tourType) {
        return virtualTourRepository.findByTourTypeAndStatus(tourType, "ACTIVE");
    }
    
    public List<VirtualTour> getToursByEntity(Long entityId, String tourType) {
        return virtualTourRepository.findByEntityIdAndTourType(entityId, tourType);
    }
    
    public VirtualTour getTourById(Long id) {
        return virtualTourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Virtual tour not found"));
    }
    
    public VirtualTour getTourByEntity(Long entityId, String tourType) {
        return virtualTourRepository.findByEntityIdAndTourTypeAndStatus(entityId, tourType, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Virtual tour not found"));
    }
    
    @Transactional
    public VirtualTour createTour(VirtualTour tour) {
        tour.setStatus("ACTIVE");
        tour.setViewCount(0);
        tour.setAverageRating(0.0);
        tour.setTotalRatings(0);
        return virtualTourRepository.save(tour);
    }
    
    @Transactional
    public VirtualTour updateTour(Long id, VirtualTour tourDetails) {
        VirtualTour tour = getTourById(id);
        
        if (tourDetails.getTitle() != null) tour.setTitle(tourDetails.getTitle());
        if (tourDetails.getDescription() != null) tour.setDescription(tourDetails.getDescription());
        if (tourDetails.getTourUrl() != null) tour.setTourUrl(tourDetails.getTourUrl());
        if (tourDetails.getThumbnailUrl() != null) tour.setThumbnailUrl(tourDetails.getThumbnailUrl());
        if (tourDetails.getPanoramaImageUrl() != null) tour.setPanoramaImageUrl(tourDetails.getPanoramaImageUrl());
        if (tourDetails.getVrVideoUrl() != null) tour.setVrVideoUrl(tourDetails.getVrVideoUrl());
        if (tourDetails.getArModelUrl() != null) tour.setArModelUrl(tourDetails.getArModelUrl());
        if (tourDetails.getSupportsAR() != null) tour.setSupportsAR(tourDetails.getSupportsAR());
        if (tourDetails.getDurationSeconds() != null) tour.setDurationSeconds(tourDetails.getDurationSeconds());
        if (tourDetails.getHotspotCount() != null) tour.setHotspotCount(tourDetails.getHotspotCount());
        if (tourDetails.getStatus() != null) tour.setStatus(tourDetails.getStatus());
        
        return virtualTourRepository.save(tour);
    }
    
    @Transactional
    public void incrementViewCount(Long id) {
        VirtualTour tour = getTourById(id);
        tour.setViewCount(tour.getViewCount() + 1);
        virtualTourRepository.save(tour);
    }
    
    @Transactional
    public VirtualTour rateTour(Long id, Double rating) {
        if (rating < 1.0 || rating > 5.0) {
            throw new RuntimeException("Rating must be between 1.0 and 5.0");
        }
        
        VirtualTour tour = getTourById(id);
        int totalRatings = tour.getTotalRatings();
        double currentAverage = tour.getAverageRating();
        
        // Calculate new average
        double newAverage = ((currentAverage * totalRatings) + rating) / (totalRatings + 1);
        
        tour.setAverageRating(newAverage);
        tour.setTotalRatings(totalRatings + 1);
        
        return virtualTourRepository.save(tour);
    }
    
    public List<VirtualTour> getTopRatedTours(int limit) {
        return virtualTourRepository.findByStatusOrderByAverageRatingDesc("ACTIVE")
                .stream()
                .limit(limit)
                .toList();
    }
}
