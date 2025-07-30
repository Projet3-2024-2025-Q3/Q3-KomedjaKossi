package com.example.jobappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used to expose user data without sensitive information like password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String address;
    private String companyName;
    private String phoneNumber;
}
