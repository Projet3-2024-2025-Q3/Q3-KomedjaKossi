package com.example.jobappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO exposing user data without sensitive fields (e.g., password).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /** User identifier. */
    private Long id;

    /** Username. */
    private String username;

    /** Email address. */
    private String email;

    /** Assigned role. */
    private String role;

    /** First name (optional). */
    private String firstName;

    /** Last name (optional). */
    private String lastName;

    /** Address (optional). */
    private String address;

    /** Company name (optional). */
    private String companyName;

    /** Phone number (optional). */
    private String phoneNumber;
}
