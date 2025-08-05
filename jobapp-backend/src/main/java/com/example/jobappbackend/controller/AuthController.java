package com.example.jobappbackend.controller;

import com.example.jobappbackend.dto.*;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.service.AuthService;
import com.example.jobappbackend.service.JwtService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public AuthenticationManager authManager;


    private final AuthService authService;

    @Autowired
    public JwtService jwtService;

    @Autowired
    public com.example.jobappbackend.service.UserService userService;

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
