package com.healthtourism.galleryservice.repository;
import com.healthtourism.galleryservice.entity.GalleryImage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GalleryImageRepository extends MongoRepository<GalleryImage, String> {
    List<GalleryImage> findByIsActiveTrue();
    List<GalleryImage> findByImageTypeAndIsActiveTrue(String imageType);
    List<GalleryImage> findByImageTypeAndRelatedIdAndIsActiveTrue(String imageType, Long relatedId);
    List<GalleryImage> findByImageTypeAndRelatedIdAndIsActiveTrueOrderByDisplayOrderAsc(String imageType, Long relatedId);
}

