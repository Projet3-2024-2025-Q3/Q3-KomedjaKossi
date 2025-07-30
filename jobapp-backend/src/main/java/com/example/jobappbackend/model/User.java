package com.example.jobappbackend.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a user in the JobApp system.
 * A user can be an administrator, company, or student.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Jobapp_user")
public class User {

    /**
     * Unique identifier for the user (primary key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username used for login.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Hashed password used for authentication.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Role of the user (e.g., ADMIN, COMPANY, STUDENT).
     */
    @Column(nullable = false)
    private String role;

    /**
     * Email address of the user.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * First name of the user (for students or company contact person).
     */
    private String firstName;

    /**
     * Last name of the user (for students or company contact person).
     */
    private String lastName;

    /**
     * Company name (only for users with role COMPANY).
     */
    private String companyName;

    /**
     * Address of the user or the company.
     */
    private String address;

    /**
     * Optional phone number.
     */
    private String phoneNumber;
}
