package com.healthtourism.filestorageservice.controller;

import com.healthtourism.filestorageservice.dto.FileMetadataDTO;
import com.healthtourism.filestorageservice.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileStorageController {
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/upload")
    public ResponseEntity<FileMetadataDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String uploadedBy,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String category) {
        try {
            return ResponseEntity.ok(fileStorageService.uploadFile(file, uploadedBy, serviceName, category));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        try {
            byte[] fileContent = fileStorageService.downloadFile(fileId);
            FileMetadataDTO metadata = fileStorageService.getFileMetadata(fileId);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalFileName() + "\"")
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        try {
            fileStorageService.deleteFile(fileId);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/service/{serviceName}")
    public ResponseEntity<List<FileMetadataDTO>> getFilesByService(@PathVariable String serviceName) {
        return ResponseEntity.ok(fileStorageService.getFilesByService(serviceName));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<FileMetadataDTO>> getFilesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(fileStorageService.getFilesByCategory(category));
    }
}

