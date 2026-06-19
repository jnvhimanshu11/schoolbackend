package com.atlas.academy.repository;

import com.atlas.academy.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, String> {
    // Spring Data derives the query from the field name publicVisible
    List<Setting> findByPublicVisibleTrue();
}
