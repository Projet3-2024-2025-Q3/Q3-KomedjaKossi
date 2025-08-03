package com.example.jobappbackend.service;

import com.example.jobappbackend.dto.ChangePasswordRequest;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service class responsible for authentication-related logic.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Resets the user's password and sends a temporary one via email.
     *
     * @param email The user's email address.
     * @throws MessagingException If an error occurs while sending the email.
     */
    public void resetPassword(String email) throws MessagingException {
        Optional<User> optional = userRepository.findByEmail(email);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("No user found with this email address.");
        }

        User user = optional.get();
        String newPassword = UUID.randomUUID().toString().substring(0, 10);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        emailService.sendSimpleMessage(
                user.getEmail(),
                "Password Reset",
                "Your new temporary password is: " + newPassword
        );
    }
    /**
     * Changes the user's password after verifying the old one.
     *
     * @param request Object containing username, old password and new password.
     */
    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

}
