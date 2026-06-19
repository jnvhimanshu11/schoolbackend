package com.atlas.academy.controller;

import com.atlas.academy.dto.ApiResponse;
import com.atlas.academy.dto.notice.NoticeRequest;
import com.atlas.academy.entity.Notice;
import com.atlas.academy.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Matches frontend src/services/api.js -> noticeApi:
 *   getAll:  GET    /notices
 *   create:  POST   /notices
 *   update:  PUT    /notices/{id}
 *   delete:  DELETE /notices/{id}
 */
@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notice>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(noticeService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Notice>> create(@Valid @RequestBody NoticeRequest request) {
        Notice saved = noticeService.create(request);
        return ResponseEntity.ok(ApiResponse.success("Notice created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Notice>> update(
            @PathVariable Long id, @Valid @RequestBody NoticeRequest request) {
        Notice updated = noticeService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Notice updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Notice deleted", null));
    }
}
