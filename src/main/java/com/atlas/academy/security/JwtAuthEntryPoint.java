package com.atlas.academy.security;

import com.atlas.academy.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Whenever an unauthenticated request hits a protected endpoint, Spring
 * Security normally returns a generic 401 HTML/whitelabel page. This makes
 * it return our standard JSON envelope instead, matching what the frontend's
 * axios interceptor expects (it checks error.response?.status === 401).
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                          HttpServletResponse response,
                          AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Object> body = ApiResponse.error("Unauthorized: please login again");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
