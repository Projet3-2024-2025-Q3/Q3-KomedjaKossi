package com.example.jobappbackend.dto;

import lombok.*;

/**
 * DTO used to carry password change information.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    private String username;
    private String oldPassword;
    private String newPassword;
}