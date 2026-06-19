package com.atlas.academy.controller;

import com.atlas.academy.dto.ApiResponse;
import com.atlas.academy.dto.PagedResponse;
import com.atlas.academy.dto.admission.AdmissionRequest;
import com.atlas.academy.entity.Admission;
import com.atlas.academy.service.AdmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Matches frontend src/services/api.js -> admissionApi:
 *   submit:       POST   /admissions
 *   getAll:       GET    /admissions?page=&status=
 *   updateStatus: PUT    /admissions/{id}/status?status=
 *   delete:       DELETE /admissions/{id}
 */
@RestController
@RequestMapping("/admissions")
@RequiredArgsConstructor
public class AdmissionController {

    private final AdmissionService admissionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Admission>> submit(@Valid @RequestBody AdmissionRequest request) {
        Admission saved = admissionService.submit(request);
        return ResponseEntity.ok(ApiResponse.success("Admission submitted successfully", saved));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Admission>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status) {
        PagedResponse<Admission> result = admissionService.getAll(page, status);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Admission>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Admission updated = admissionService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        admissionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Admission deleted", null));
    }
}
