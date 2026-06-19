package com.atlas.academy.service;

import com.atlas.academy.dto.auth.LoginResponse;
import com.atlas.academy.entity.Admin;
import com.atlas.academy.entity.OtpToken;
import com.atlas.academy.exception.AuthException;
import com.atlas.academy.repository.AdminRepository;
import com.atlas.academy.repository.OtpTokenRepository;
import com.atlas.academy.security.JwtUtil;
import com.atlas.academy.util.OtpGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final int MAX_VERIFY_ATTEMPTS = 5;

    private final AdminRepository adminRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final OtpGenerator otpGenerator;
    private final MailService mailService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.otp.expiry-minutes}")
    private int otpExpiryMinutes;

    @Value("${app.otp.resend-cooldown-seconds}")
    private int resendCooldownSeconds;

    /**
     * Step 1 of login: admin submits their email.
     * If it belongs to a registered admin, generate + email an OTP.
     *
     * IMPORTANT: we always return the same generic success message whether
     * or not the email is a real admin, so attackers can't use this endpoint
     * to discover which emails are valid admins.
     */
    @Transactional
    public void requestOtp(String email) {
        Optional<Admin> adminOpt = adminRepository.findByEmailIgnoreCaseAndActiveTrue(email);

        if (adminOpt.isEmpty()) {
            log.warn("OTP requested for unknown/inactive admin email: {}", email);
            return; // silently no-op - controller still returns a generic success message
        }

        // Basic resend cooldown so the same email can't be spammed with requests
        Optional<OtpToken> lastToken = otpTokenRepository.findFirstByEmailIgnoreCaseOrderByCreatedAtDesc(email);
        if (lastToken.isPresent()) {
            long secondsSinceLast = ChronoUnit.SECONDS.between(lastToken.get().getCreatedAt(), LocalDateTime.now());
            if (secondsSinceLast < resendCooldownSeconds) {
                long wait = resendCooldownSeconds - secondsSinceLast;
                throw new AuthException("Please wait " + wait + " seconds before requesting another code.");
            }
        }

        String otp = otpGenerator.generate();

        OtpToken token = OtpToken.builder()
                .email(email.toLowerCase())
                .otpHash(passwordEncoder.encode(otp))
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .used(false)
                .attemptCount(0)
                .build();

        otpTokenRepository.save(token);
        mailService.sendOtpEmail(email, otp);
    }

    /**
     * Step 2 of login: admin submits the OTP they received by email.
     * On success, issues a JWT.
     */
    @Transactional
    public LoginResponse verifyOtp(String email, String otp) {
        Admin admin = adminRepository.findByEmailIgnoreCaseAndActiveTrue(email)
                .orElseThrow(() -> new AuthException("Invalid email or code"));

        OtpToken token = otpTokenRepository
                .findFirstByEmailIgnoreCaseAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new AuthException("No active code found. Please request a new one."));

        if (token.isUsed()) {
            throw new AuthException("This code has already been used. Please request a new one.");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthException("This code has expired. Please request a new one.");
        }

        if (token.getAttemptCount() >= MAX_VERIFY_ATTEMPTS) {
            throw new AuthException("Too many incorrect attempts. Please request a new code.");
        }

        if (!passwordEncoder.matches(otp, token.getOtpHash())) {
            token.setAttemptCount(token.getAttemptCount() + 1);
            otpTokenRepository.save(token);
            throw new AuthException("Incorrect code. Please try again.");
        }

        // Success - mark token as used so it can't be replayed
        token.setUsed(true);
        otpTokenRepository.save(token);

        String jwt = jwtUtil.generateToken(admin.getEmail());
        return new LoginResponse(jwt, admin.getEmail(), admin.getName());
    }
}
