package com.atlas.academy.controller;

import com.atlas.academy.dto.ApiResponse;
import com.atlas.academy.dto.faculty.FacultyRequest;
import com.atlas.academy.entity.Faculty;
import com.atlas.academy.service.FacultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Matches frontend src/services/api.js -> facultyApi:
 *   getAll:  GET    /faculty
 *   create:  POST   /faculty
 *   update:  PUT    /faculty/{id}
 *   delete:  DELETE /faculty/{id}
 */
@RestController
@RequestMapping("/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Faculty>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(facultyService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Faculty>> create(@Valid @RequestBody FacultyRequest request) {
        Faculty saved = facultyService.create(request);
        return ResponseEntity.ok(ApiResponse.success("Faculty added", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Faculty>> update(
            @PathVariable Long id, @Valid @RequestBody FacultyRequest request) {
        Faculty updated = facultyService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Faculty updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        facultyService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Faculty removed", null));
    }
}
