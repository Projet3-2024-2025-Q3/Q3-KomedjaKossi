package com.example.jobappbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data

@Table(name = "Jobapp_user")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    private String email;
}
