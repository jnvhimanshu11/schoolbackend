package com.atlas.academy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends emails via the Gmail SMTP server configured in application.properties
 * (spring.mail.* properties). Requires a Gmail "App Password", not your
 * normal Gmail account password - see application.properties comments.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Value("${app.otp.expiry-minutes}")
    private int otpExpiryMinutes;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("Your ATLAS Academy Admin Login Code");
        message.setText(buildOtpEmailBody(otp));

        try {
            mailSender.send(message);
            log.info("OTP email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send OTP email. Please try again later.");
        }
    }

    private String buildOtpEmailBody(String otp) {
        return """
                Hello,

                Your one-time login code for the ATLAS Academy Admin Panel is:

                    %s

                This code will expire in %d minutes. If you did not request this code, you can safely ignore this email.

                — ATLAS Academy
                """.formatted(otp, otpExpiryMinutes);
    }
}
