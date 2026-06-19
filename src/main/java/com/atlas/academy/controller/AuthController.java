package com.atlas.academy.controller;

import com.atlas.academy.dto.ApiResponse;
import com.atlas.academy.dto.auth.LoginResponse;
import com.atlas.academy.dto.auth.RequestOtpRequest;
import com.atlas.academy.dto.auth.VerifyOtpRequest;
import com.atlas.academy.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Step 1: admin enters their email -> we email them a 6-digit OTP. */
    @PostMapping("/request-otp")
    public ResponseEntity<ApiResponse<Void>> requestOtp(@Valid @RequestBody RequestOtpRequest request) {
        authService.requestOtp(request.getEmail());
        // Always the same generic message, regardless of whether the email
        // is actually a registered admin - prevents email enumeration.
        return ResponseEntity.ok(
                ApiResponse.success("If this email is registered, a login code has been sent.", null)
        );
    }

    /** Step 2: admin enters the OTP -> we verify it and issue a JWT. */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        LoginResponse response = authService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
