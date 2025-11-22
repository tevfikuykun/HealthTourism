package com.healthtourism.galleryservice.repository;
import com.healthtourism.galleryservice.entity.GalleryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {
    List<GalleryImage> findByIsActiveTrue();
    List<GalleryImage> findByImageTypeAndIsActiveTrue(String imageType);
    List<GalleryImage> findByImageTypeAndRelatedIdAndIsActiveTrue(String imageType, Long relatedId);
    @Query("SELECT g FROM GalleryImage g WHERE g.imageType = :imageType AND g.relatedId = :relatedId AND g.isActive = true ORDER BY g.displayOrder ASC")
    List<GalleryImage> findByImageTypeAndRelatedIdOrderByDisplayOrder(@Param("imageType") String imageType, @Param("relatedId") Long relatedId);
}

