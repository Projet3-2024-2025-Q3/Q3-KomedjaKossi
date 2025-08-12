package com.example.jobappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO used to register a new user account.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    /** Chosen username. */
    private String username;

    /** Email address. */
    private String email;

    /** Plain password to be encoded. */
    private String password;

    /** Assigned role (e.g., STUDENT, COMPANY, ADMIN). */
    private String role;

    private String firstName;

    private String lastName;

    private String address;

    private String companyName;

    private String phoneNumber;
}
