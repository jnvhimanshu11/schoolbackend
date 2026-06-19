package com.atlas.academy.exception;

/** Thrown for any auth-related failure: invalid/expired OTP, unknown admin, etc. */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
