package com.atlas.academy.dto.faculty;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FacultyRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Designation is required")
    private String designation;

    private String subject;

    private String photoUrl;

    private String bio;
}
