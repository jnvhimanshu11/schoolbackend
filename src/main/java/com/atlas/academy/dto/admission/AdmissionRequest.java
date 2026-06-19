package com.atlas.academy.dto.admission;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AdmissionRequest {

    @NotBlank(message = "Student name is required")
    private String studentName;

    @NotBlank(message = "Parent name is required")
    private String parentName;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    private String email;

    @NotBlank(message = "Please select a program")
    private String classProgram;

    private LocalDate dateOfBirth;

    @NotBlank(message = "Address is required")
    private String address;

    private String prevSchool;

    private String message;
}
