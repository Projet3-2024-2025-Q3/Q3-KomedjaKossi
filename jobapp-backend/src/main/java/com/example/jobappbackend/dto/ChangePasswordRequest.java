package com.example.jobappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO carrying data required to change a user's password.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    /** Target username. */
    private String username;

    /** Current password to verify identity. */
    private String oldPassword;

    /** New password to be set. */
    private String newPassword;
}
