package com.healthtourism.filestorageservice.controller;

import com.healthtourism.filestorageservice.dto.FileMetadataDTO;
import com.healthtourism.filestorageservice.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
@Tag(name = "File Storage", description = "File upload and management APIs")
public class FileStorageController {
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/upload")
    @Operation(summary = "Upload a file", description = "Uploads a file with optional metadata")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or validation failed")
    })
    public ResponseEntity<FileMetadataDTO> uploadFile(
            @Parameter(description = "File to upload", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "User ID who uploaded the file")
            @RequestParam(required = false) String uploadedBy,
            @Parameter(description = "Service name (e.g., hospital-service)")
            @RequestParam(required = false) String serviceName,
            @Parameter(description = "File category (e.g., IMAGE_HOSPITAL, MEDICAL_DOCUMENT)")
            @RequestParam(required = false) String category,
            HttpServletRequest request) {
        try {
            // Get user ID from request attributes (set by JWT filter)
            String userId = (String) request.getAttribute("userId");
            if (uploadedBy == null && userId != null) {
                uploadedBy = userId;
            }
            
            return ResponseEntity.ok(fileStorageService.uploadFile(file, uploadedBy, serviceName, category));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/upload/image/{entityType}/{entityId}")
    @Operation(summary = "Upload image for Hospital or Doctor", description = "Uploads an image specifically for Hospital or Doctor entity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid image or validation failed")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<FileMetadataDTO> uploadImage(
            @Parameter(description = "Entity type (hospital or doctor)", required = true)
            @PathVariable String entityType,
            @Parameter(description = "Entity ID", required = true)
            @PathVariable Long entityId,
            @Parameter(description = "Image file", required = true)
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            return ResponseEntity.ok(fileStorageService.uploadImage(file, userId, entityType, entityId));
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

