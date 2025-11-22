package com.healthtourism.filestorageservice.repository;

import com.healthtourism.filestorageservice.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    Optional<FileMetadata> findByFileName(String fileName);
    List<FileMetadata> findByServiceName(String serviceName);
    List<FileMetadata> findByCategory(String category);
    List<FileMetadata> findByUploadedBy(String uploadedBy);
}

