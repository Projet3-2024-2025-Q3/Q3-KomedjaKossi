package com.example.jobappbackend.controller;

import com.example.jobappbackend.dto.*;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that handles user authentication and registration.
 * Provides endpoints for login and user creation with JWT token generation.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private com.example.jobappbackend.service.UserService userService;

    /**
     * Authenticates a user with provided credentials and returns a JWT token if successful.
     *
     * @param request AuthRequest containing username and password.
     * @return AuthResponse containing the JWT token.
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        UsernamePasswordAuthenticationToken authInput = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword());
        authManager.authenticate(authInput);

        UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails.getUsername());
        return new AuthResponse(token);
    }

    /**
     * Registers a new user account with the provided data.
     *
     * @param request RegisterRequest containing username, email, password, and role.
     * @return User object representing the newly registered user.
     */
    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }
}
