package com.atlas.academy.repository;

import com.atlas.academy.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    /** Most recent, still-unused OTP for an email - used during verification. */
    Optional<OtpToken> findFirstByEmailIgnoreCaseAndUsedFalseOrderByCreatedAtDesc(String email);

    /** Most recent OTP of any state for an email - used to enforce resend cooldown. */
    Optional<OtpToken> findFirstByEmailIgnoreCaseOrderByCreatedAtDesc(String email);

    @Modifying
    @Query("DELETE FROM OtpToken o WHERE o.expiresAt < :cutoff")
    void deleteAllExpiredBefore(@Param("cutoff") LocalDateTime cutoff);
}
