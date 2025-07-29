package com.example.jobappbackend.dto;

import lombok.*;

/**
 * Data Transfer Object for login credentials.
 * Contains the username and password provided by the user.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    /**
     * Username provided by the user.
     */
    private String username;

    /**
     * Password provided by the user.
     */
    private String password;
}
