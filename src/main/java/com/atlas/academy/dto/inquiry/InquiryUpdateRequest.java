package com.atlas.academy.dto.inquiry;

import lombok.Data;

/** Used by admin to mark an inquiry resolved/unresolved or edit details. */
@Data
public class InquiryUpdateRequest {
    private String name;
    private String mobile;
    private String email;
    private String subject;
    private String message;
    private Boolean resolved;
}
