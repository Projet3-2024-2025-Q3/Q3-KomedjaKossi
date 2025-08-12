package com.example.jobappbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO carrying login credentials.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "password")
public class AuthRequest {

    /** Username provided by the user. */
    private String username;

    /** Password provided by the user. */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
