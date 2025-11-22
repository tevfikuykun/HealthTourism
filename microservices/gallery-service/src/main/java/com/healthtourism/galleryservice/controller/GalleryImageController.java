package com.healthtourism.galleryservice.controller;
import com.healthtourism.galleryservice.dto.GalleryImageDTO;
import com.healthtourism.galleryservice.entity.GalleryImage;
import com.healthtourism.galleryservice.service.GalleryImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gallery")
@CrossOrigin(origins = "*")
public class GalleryImageController {
    @Autowired
    private GalleryImageService galleryImageService;
    
    @GetMapping("/type/{imageType}")
    public ResponseEntity<List<GalleryImageDTO>> getImagesByType(@PathVariable String imageType) {
        return ResponseEntity.ok(galleryImageService.getImagesByType(imageType));
    }
    
    @GetMapping("/type/{imageType}/related/{relatedId}")
    public ResponseEntity<List<GalleryImageDTO>> getImagesByTypeAndRelatedId(@PathVariable String imageType, @PathVariable Long relatedId) {
        return ResponseEntity.ok(galleryImageService.getImagesByTypeAndRelatedId(imageType, relatedId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GalleryImageDTO> getImageById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(galleryImageService.getImageById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<GalleryImageDTO> createImage(@RequestBody GalleryImage image) {
        return ResponseEntity.ok(galleryImageService.createImage(image));
    }
}

