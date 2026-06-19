package com.atlas.academy.controller;

import com.atlas.academy.dto.ApiResponse;
import com.atlas.academy.dto.PagedResponse;
import com.atlas.academy.dto.inquiry.InquiryRequest;
import com.atlas.academy.dto.inquiry.InquiryUpdateRequest;
import com.atlas.academy.entity.Inquiry;
import com.atlas.academy.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Matches frontend src/services/api.js -> inquiryApi:
 *   submit:  POST   /inquiries
 *   getAll:  GET    /inquiries?page=
 *   update:  PUT    /inquiries/{id}
 *   delete:  DELETE /inquiries/{id}
 */
@RestController
@RequestMapping("/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<ApiResponse<Inquiry>> submit(@Valid @RequestBody InquiryRequest request) {
        Inquiry saved = inquiryService.submit(request);
        return ResponseEntity.ok(ApiResponse.success("Message sent successfully", saved));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Inquiry>>> getAll(
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(ApiResponse.success(inquiryService.getAll(page)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Inquiry>> update(
            @PathVariable Long id,
            @RequestBody InquiryUpdateRequest request) {
        Inquiry updated = inquiryService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Inquiry updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        inquiryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Inquiry deleted", null));
    }
}
