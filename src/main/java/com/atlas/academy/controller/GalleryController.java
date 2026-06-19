package com.atlas.academy.controller;

import com.atlas.academy.dto.ApiResponse;
import com.atlas.academy.entity.GalleryItem;
import com.atlas.academy.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Matches frontend src/services/api.js -> galleryApi:
 *   getAll:  GET    /gallery?category=
 *   upload:  POST   /gallery (multipart/form-data)
 *   delete:  DELETE /gallery/{id}
 */
@RestController
@RequestMapping("/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GalleryItem>>> getAll(
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(ApiResponse.success(galleryService.getAll(category)));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<GalleryItem>> upload(
            @RequestParam String title,
            @RequestParam(required = false) String category,
            @RequestParam("file") MultipartFile file) {
        GalleryItem saved = galleryService.upload(title, category, file);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        galleryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Image deleted", null));
    }
}
