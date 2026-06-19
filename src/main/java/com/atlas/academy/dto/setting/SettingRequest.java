package com.atlas.academy.dto.setting;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SettingRequest {

    @NotBlank(message = "Key is required")
    private String key;

    private String value;

    /** Optional - defaults to true (public) for new keys if not provided. */
    private Boolean isPublic;
}
