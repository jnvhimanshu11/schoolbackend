package com.atlas.academy.service;

import com.atlas.academy.entity.GalleryItem;
import com.atlas.academy.exception.ResourceNotFoundException;
import com.atlas.academy.repository.GalleryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryItemRepository galleryItemRepository;
    private final FileStorageService fileStorageService;

    public List<GalleryItem> getAll(String category) {
        if (category != null && !category.isBlank()) {
            return galleryItemRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc(category);
        }
        return galleryItemRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public GalleryItem upload(String title, String category, MultipartFile file) {
        String imageUrl = fileStorageService.store(file);

        GalleryItem item = GalleryItem.builder()
                .title(title)
                .category(category)
                .imageUrl(imageUrl)
                .build();

        return galleryItemRepository.save(item);
    }

    @Transactional
    public void delete(Long id) {
        GalleryItem item = galleryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gallery item not found with id " + id));
        fileStorageService.delete(item.getImageUrl());
        galleryItemRepository.delete(item);
    }
}
