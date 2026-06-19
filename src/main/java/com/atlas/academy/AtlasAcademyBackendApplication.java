package com.atlas.academy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the ATLAS Academy backend.
 *
 * @EnableScheduling -> lets us run a background job that cleans up
 * expired OTPs periodically (see OtpCleanupScheduler).
 */
@SpringBootApplication
@EnableScheduling
public class AtlasAcademyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtlasAcademyBackendApplication.class, args);
    }
}
