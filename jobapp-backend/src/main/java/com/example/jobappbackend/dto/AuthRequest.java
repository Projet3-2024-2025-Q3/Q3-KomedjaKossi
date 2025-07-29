package com.example.jobappbackend.dto;

import lombok.*;

@Getter
@Setter
public class AuthRequest {
    private String username;
    private String password;
}
