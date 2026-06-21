package com.atlas.academy.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Sends emails via the Resend HTTPS API (https://resend.com).
 *
 * We switched away from Gmail SMTP because Railway (and most cloud hosts)
 * block outbound SMTP ports (25/465/587) by default to prevent spam abuse,
 * which caused OTP emails to silently time out. Resend's API runs over plain
 * HTTPS (port 443), which is never blocked.
 *
 * Required config (see application.properties / Railway env vars):
 *   app.resend.api-key   - your Resend API key (starts with "re_")
 *   app.resend.from      - the "from" address, e.g. "ATLAS Academy <noreply@thebohothread.in>"
 *                           Must be on a domain verified in Resend, OR use
 *                           "onboarding@resend.dev" for testing (only delivers
 *                           to your own Resend account email in that case).
 */
@Service
@Slf4j
public class MailService {

    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.resend.api-key}")
    private String resendApiKey;

    @Value("${app.resend.from}")
    private String fromAddress;

    @Value("${app.otp.expiry-minutes}")
    private int otpExpiryMinutes;

    public void sendOtpEmail(String toEmail, String otp) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("from", fromAddress);
        body.put("to", new String[]{toEmail});
        body.put("subject", "Your ATLAS Academy Admin Login Code");
        body.put("text", buildOtpEmailBody(otp));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            var response = restTemplate.postForEntity(RESEND_API_URL, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Resend returned non-success status {} for {}: {}",
                        response.getStatusCode(), toEmail, response.getBody());
                throw new RuntimeException("Failed to send OTP email. Please try again later.");
            }
            log.info("OTP email sent to {} via Resend", toEmail);
        } catch (RestClientException e) {
            log.error("Failed to send OTP email to {} via Resend: {}", toEmail, e.getMessage());
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