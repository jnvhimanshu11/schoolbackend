package com.atlas.academy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple key-value store for site settings (e.g. site title, contact phone,
 * social links, maintenance mode, etc.)
 *
 * publicVisible controls whether this setting is returned by the unauthenticated
 * GET /settings/public endpoint (consumed by the public site) or only via
 * GET /settings (admin-only).
 *
 * NOTE: We deliberately avoid naming this field `isPublic` (a boolean with "is"
 * prefix) because Lombok @Data strips the prefix and generates isPublic()/setPublic()
 * instead of the expected getIsPublic()/setIsPublic(), causing confusing call-site errors.
 */
@Entity
@Table(name = "settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Setting {

    @Id
    @Column(name = "setting_key", nullable = false, unique = true)
    private String key;

    @Column(name = "setting_value", length = 2000)
    private String value;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private boolean publicVisible = true;
}
