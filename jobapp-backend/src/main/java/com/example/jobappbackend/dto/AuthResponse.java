package com.example.jobappbackend.dto;

import lombok.*;

/**
 * Data Transfer Object for JWT authentication response.
 * Contains the generated token after successful login.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    /**
     * JWT token issued after successful authentication.
     */
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
