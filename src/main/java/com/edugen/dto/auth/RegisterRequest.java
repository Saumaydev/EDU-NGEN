package com.edugen.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "User type is required")
    private String userType; // STUDENT or TEACHER

    @NotBlank(message = "School ID is required")
    private String schoolId;

    // For students
    @Email(message = "Parent email should be valid")
    private String parentEmail;

    private String classSection;
    private String rollNumber;

    // For teachers
    private String subject;
    private String department;
}
