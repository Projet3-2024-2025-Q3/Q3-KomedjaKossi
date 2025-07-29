package com.example.jobappbackend.dto;

import lombok.*;

/**
 * Data Transfer Object for user registration.
 * Contains the required fields to create a new user account.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    /**
     * Username chosen by the user.
     */
    private String username;

    /**
     * Email address of the user.
     */
    private String email;

    /**
     * Password chosen by the user.
     */
    private String password;

    /**
     * Role assigned to the user (e.g., STUDENT, COMPANY, ADMIN).
     */
    private String role;
}
