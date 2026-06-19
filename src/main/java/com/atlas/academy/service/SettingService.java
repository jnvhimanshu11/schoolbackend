package com.atlas.academy.service;

import com.atlas.academy.dto.setting.SettingRequest;
import com.atlas.academy.entity.Setting;
import com.atlas.academy.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;

    /** Public site-facing settings only (no login required). */
    public Map<String, String> getPublicSettings() {
        return settingRepository.findByPublicVisibleTrue().stream()
                .collect(Collectors.toMap(
                        Setting::getKey,
                        s -> s.getValue() == null ? "" : s.getValue()
                ));
    }

    /** All settings — admin only. */
    public List<Setting> getAll() {
        return settingRepository.findAll();
    }

    /**
     * Create or update a single setting.
     * Matches frontend settingsApi.update({ key, value, isPublic? })
     */
    @Transactional
    public Setting upsert(SettingRequest request) {
        Setting setting = settingRepository.findById(request.getKey())
                .orElse(Setting.builder()
                        .key(request.getKey())
                        // default to visible for new keys unless caller says otherwise
                        .publicVisible(request.getIsPublic() == null || request.getIsPublic())
                        .build());

        setting.setValue(request.getValue());
        if (request.getIsPublic() != null) {
            setting.setPublicVisible(request.getIsPublic());
        }

        return settingRepository.save(setting);
    }
}
