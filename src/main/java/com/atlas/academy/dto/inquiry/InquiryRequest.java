package com.atlas.academy.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InquiryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    private String email;

    private String subject;

    @NotBlank(message = "Message is required")
    private String message;
}
