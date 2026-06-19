package com.atlas.academy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Maps directly to the fields submitted by src/pages/Admission.jsx
 */
@Entity
@Table(name = "admissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "parent_name", nullable = false)
    private String parentName;

    @Column(nullable = false)
    private String mobile;

    private String email;

    @Column(name = "class_program", nullable = false)
    private String classProgram;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 1000)
    private String address;

    @Column(name = "prev_school")
    private String prevSchool;

    @Column(length = 2000)
    private String message;

    /** PENDING, APPROVED, REJECTED, CONTACTED etc. Kept as a free string so the
     *  admin UI can introduce new statuses without a DB migration. */
    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "PENDING";
    }
}
