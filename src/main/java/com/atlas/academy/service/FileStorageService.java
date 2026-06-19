package com.atlas.academy.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Stores uploaded gallery images on local disk under app.upload.dir,
 * and serves them back via /api/uploads/** (see WebConfig).
 *
 * For production behind a real domain, consider swapping this for
 * cloud storage (S3, Cloudinary etc) - the interface stays the same.
 */
@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private Path storagePath;

    @PostConstruct
    public void init() {
        this.storagePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + storagePath, e);
        }
    }

    /** Saves the file and returns a relative URL path like "/uploads/abc123.jpg" */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null
                ? file.getOriginalFilename() : "file");
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }

        String storedFilename = UUID.randomUUID() + extension;

        try {
            Path target = storagePath.resolve(storedFilename);
            file.transferTo(target);
            return "/uploads/" + storedFilename;
        } catch (IOException e) {
            log.error("Failed to store file {}: {}", originalFilename, e.getMessage());
            throw new RuntimeException("Failed to store uploaded file");
        }
    }

    public void delete(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/uploads/")) return;
        try {
            String filename = imageUrl.substring("/uploads/".length());
            Files.deleteIfExists(storagePath.resolve(filename));
        } catch (IOException e) {
            log.warn("Could not delete file for {}: {}", imageUrl, e.getMessage());
        }
    }
}
