package com.example.booking.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary cloudinary;

    private Cloudinary getCloudinary() {
        if (cloudinary == null) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret));
        }
        return cloudinary;
    }

    public String uploadAvatar(MultipartFile file) throws IOException {
        // 1. Validate file (size, type) - can be done here or in controller

        // 2. Compress image using Thumbnailator
        File compressedFile = compressImage(file);

        try {
            // 3. Upload to Cloudinary
            Map<?, ?> uploadResult = getCloudinary().uploader().upload(compressedFile, ObjectUtils.asMap(
                    "folder", "avatars",
                    "public_id", UUID.randomUUID().toString(),
                    "resource_type", "image"));

            return (String) uploadResult.get("secure_url");
        } finally {
            // 4. Clean up temporary file
            if (compressedFile.exists()) {
                compressedFile.delete();
            }
        }
    }

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // 1. Validate file (size, type) - can be done here or in controller

        // 2. Compress image using Thumbnailator (assuming it's an image for now)
        File compressedFile = compressImage(file);

        try {
            // 3. Upload to Cloudinary
            Map<?, ?> uploadResult = getCloudinary().uploader().upload(compressedFile, ObjectUtils.asMap(
                    "folder", folder,
                    "public_id", UUID.randomUUID().toString(),
                    "resource_type", "auto"));

            return (String) uploadResult.get("secure_url");
        } finally {
            // 4. Clean up temporary file
            if (compressedFile.exists()) {
                compressedFile.delete();
            }
        }
    }

    private File compressImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = ".jpg";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        Path tempPath = Files.createTempFile("upload_", extension);
        File tempFile = tempPath.toFile();

        // Compress logic: Resize to max 1200px (larger for content), quality 0.8
        // Thumbnailator automatically handles the output format based on the file
        // extension
        Thumbnails.of(file.getInputStream())
                .size(1200, 1200) // Max width/height for content
                .outputQuality(0.8)
                .toFile(tempFile);

        return tempFile;
    }
}
