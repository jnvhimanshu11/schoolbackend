package com.atlas.academy.config;

import com.atlas.academy.entity.Admin;
import com.atlas.academy.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * On application startup, ensures at least one admin exists so you're never
 * locked out. Configure the email via app.seed.admin-email in
 * application.properties (or just edit it directly in the DB later -
 * there is no signup endpoint, admins are managed manually).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;

    @Value("${app.seed.admin-email:admin@atlasacademy.in}")
    private String seedAdminEmail;

    @Value("${app.seed.admin-name:Admin}")
    private String seedAdminName;

    @Override
    public void run(String... args) {
        if (!adminRepository.existsByEmailIgnoreCase(seedAdminEmail)) {
            Admin admin = Admin.builder()
                    .email(seedAdminEmail)
                    .name(seedAdminName)
                    .active(true)
                    .build();
            adminRepository.save(admin);
            log.info("Seeded default admin: {}", seedAdminEmail);
        }
    }
}
