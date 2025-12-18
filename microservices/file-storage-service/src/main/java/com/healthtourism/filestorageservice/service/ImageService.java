package com.healthtourism.filestorageservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Service for image processing and validation
 */
@Service
public class ImageService {
    
    @Value("${image.allowed-formats:jpg,jpeg,png,gif,webp}")
    private String allowedFormats;
    
    @Value("${image.max-width:2048}")
    private int maxWidth;
    
    @Value("${image.max-height:2048}")
    private int maxHeight;
    
    @Value("${image.thumbnail-width:300}")
    private int thumbnailWidth;
    
    @Value("${image.thumbnail-height:300}")
    private int thumbnailHeight;
    
    /**
     * Validates if the uploaded file is a valid image
     */
    public void validateImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }
        
        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Invalid file name");
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        List<String> allowed = Arrays.asList(allowedFormats.split(","));
        
        if (!allowed.contains(extension)) {
            throw new IllegalArgumentException("Image format not allowed. Allowed formats: " + allowedFormats);
        }
        
        // Validate image content
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (image == null) {
                throw new IllegalArgumentException("Invalid image file");
            }
            
            // Check dimensions
            if (image.getWidth() > maxWidth || image.getHeight() > maxHeight) {
                throw new IllegalArgumentException(
                    String.format("Image dimensions exceed maximum allowed size (%dx%d)", maxWidth, maxHeight)
                );
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read image file: " + e.getMessage());
        }
    }
    
    /**
     * Resizes image to specified dimensions
     */
    public byte[] resizeImage(byte[] imageBytes, int targetWidth, int targetHeight, String format) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        
        if (originalImage == null) {
            throw new IOException("Invalid image data");
        }
        
        // Calculate aspect ratio
        double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
        int newWidth = targetWidth;
        int newHeight = targetHeight;
        
        if (aspectRatio > 1) {
            // Landscape
            newHeight = (int) (targetWidth / aspectRatio);
        } else {
            // Portrait or square
            newWidth = (int) (targetHeight * aspectRatio);
        }
        
        // Create resized image
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();
        
        // Convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, format, baos);
        return baos.toByteArray();
    }
    
    /**
     * Creates a thumbnail from image
     */
    public byte[] createThumbnail(byte[] imageBytes, String format) throws IOException {
        return resizeImage(imageBytes, thumbnailWidth, thumbnailHeight, format);
    }
    
    /**
     * Compresses image with specified quality
     */
    public byte[] compressImage(byte[] imageBytes, float quality, String format) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        
        if (image == null) {
            throw new IOException("Invalid image data");
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // For JPEG compression
        if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
            javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            javax.imageio.stream.ImageWriteParam param = writer.getDefaultWriteParam();
            
            if (param.canWriteCompressed()) {
                param.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);
            }
            
            javax.imageio.stream.ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            writer.setOutput(ios);
            writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
            ios.close();
            writer.dispose();
        } else {
            // For other formats, just write without compression
            ImageIO.write(image, format, baos);
        }
        
        return baos.toByteArray();
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
