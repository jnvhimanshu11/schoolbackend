package com.atlas.academy.controller;

import com.atlas.academy.dto.ApiResponse;
import com.atlas.academy.dto.setting.SettingRequest;
import com.atlas.academy.entity.Setting;
import com.atlas.academy.service.SettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Matches frontend src/services/api.js -> settingsApi:
 *   getPublic: GET /settings/public   (no auth)
 *   getAll:    GET /settings          (admin only)
 *   update:    PUT /settings { key, value }  (admin only)
 */
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPublic() {
        return ResponseEntity.ok(ApiResponse.success(settingService.getPublicSettings()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Setting>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(settingService.getAll()));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Setting>> update(@Valid @RequestBody SettingRequest request) {
        Setting saved = settingService.upsert(request);
        return ResponseEntity.ok(ApiResponse.success("Setting updated", saved));
    }
}
