package com.example.jobappbackend.controller;

import com.example.jobappbackend.dto.*;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.repository.UserRepository;
import com.example.jobappbackend.service.AuthService;
import com.example.jobappbackend.service.JwtService;
import com.example.jobappbackend.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that handles user authentication and registration.
 * Provides endpoints for login, registration, password reset, and password change.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    public AuthenticationManager authManager;

    @Autowired
    public JwtService jwtService;

    @Autowired
    public UserService userService;

    @Autowired
    public UserRepository userRepository;

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authenticates a user with provided credentials and returns a JWT token if successful.
     *
     * @param request AuthRequest containing username and password.
     * @return AuthResponse containing the JWT token.
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        // Authenticate user credentials
        UsernamePasswordAuthenticationToken authInput =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authManager.authenticate(authInput);

        User userEntity = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(userEntity);

        return new AuthResponse(token);
    }

    /**
     * Registers a new user account with the provided data.
     *
     * @param request RegisterRequest containing username, email, password, role, and profile fields.
     * @return the newly created user as a UserResponse.
     */
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    /**
     * Endpoint to allow users to reset their password using their email address.
     *
     * @param email The user's email address.
     * @return A confirmation message indicating the new password has been sent.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws MessagingException {
        authService.resetPassword(email);
        return ResponseEntity.ok("A new password has been sent to your email.");
    }

    /**
     * Allows a user to change their password.
     *
     * @param request ChangePasswordRequest containing username, old and new password.
     * @return Confirmation message.
     */
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully.");
    }
}
