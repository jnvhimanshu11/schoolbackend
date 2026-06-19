package com.atlas.academy.config;

import com.atlas.academy.repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Housekeeping job: deletes OTP tokens that expired more than a day ago,
 * so the otp_tokens table doesn't grow forever.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OtpCleanupScheduler {

    private final OtpTokenRepository otpTokenRepository;

    // Runs once every 24 hours
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    @Transactional
    public void cleanupExpiredOtps() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        otpTokenRepository.deleteAllExpiredBefore(cutoff);
        log.info("Cleaned up expired OTP tokens older than {}", cutoff);
    }
}
