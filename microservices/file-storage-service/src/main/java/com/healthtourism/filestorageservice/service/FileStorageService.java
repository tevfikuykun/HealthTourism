package com.healthtourism.filestorageservice.service;

import com.healthtourism.filestorageservice.dto.FileMetadataDTO;
import com.healthtourism.filestorageservice.entity.FileMetadata;
import com.healthtourism.filestorageservice.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileStorageService {
    
    @Autowired
    private FileMetadataRepository fileMetadataRepository;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Value("${file.max-size}")
    private Long maxSize;
    
    public FileMetadataDTO uploadFile(MultipartFile file, String uploadedBy, String serviceName, String category) throws IOException {
        if (file.getSize() > maxSize) {
            throw new RuntimeException("File size exceeds maximum allowed size");
        }
        
        String originalFileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName;
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        FileMetadata metadata = new FileMetadata();
        metadata.setFileName(fileName);
        metadata.setOriginalFileName(originalFileName);
        metadata.setFilePath(filePath.toString());
        metadata.setContentType(file.getContentType());
        metadata.setFileSize(file.getSize());
        metadata.setUploadedBy(uploadedBy);
        metadata.setServiceName(serviceName);
        metadata.setCategory(category);
        
        metadata = fileMetadataRepository.save(metadata);
        return convertToDTO(metadata);
    }
    
    public byte[] downloadFile(Long fileId) throws IOException {
        FileMetadata metadata = fileMetadataRepository.findById(fileId)
            .orElseThrow(() -> new RuntimeException("File not found"));
        
        Path filePath = Paths.get(metadata.getFilePath());
        return Files.readAllBytes(filePath);
    }
    
    public void deleteFile(Long fileId) throws IOException {
        FileMetadata metadata = fileMetadataRepository.findById(fileId)
            .orElseThrow(() -> new RuntimeException("File not found"));
        
        Path filePath = Paths.get(metadata.getFilePath());
        Files.deleteIfExists(filePath);
        fileMetadataRepository.delete(metadata);
    }
    
    public List<FileMetadataDTO> getFilesByService(String serviceName) {
        return fileMetadataRepository.findByServiceName(serviceName)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<FileMetadataDTO> getFilesByCategory(String category) {
        return fileMetadataRepository.findByCategory(category)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public FileMetadataDTO getFileMetadata(Long fileId) {
        FileMetadata metadata = fileMetadataRepository.findById(fileId)
            .orElseThrow(() -> new RuntimeException("File not found"));
        return convertToDTO(metadata);
    }
    
    private FileMetadataDTO convertToDTO(FileMetadata metadata) {
        return new FileMetadataDTO(
            metadata.getId(),
            metadata.getFileName(),
            metadata.getOriginalFileName(),
            metadata.getFilePath(),
            metadata.getContentType(),
            metadata.getFileSize(),
            metadata.getUploadedBy(),
            metadata.getServiceName(),
            metadata.getCategory(),
            metadata.getUploadedAt()
        );
    }
}

