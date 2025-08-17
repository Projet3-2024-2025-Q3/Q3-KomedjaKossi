package com.example.jobappbackend.service;

import com.example.jobappbackend.dto.RegisterRequest;
import com.example.jobappbackend.dto.UserResponse;
import com.example.jobappbackend.exception.ApiException;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service responsible for handling user-related operations such as authentication and registration.
 * Implements {@link UserDetailsService} to integrate with Spring Security.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a {@code UserService} with the required dependencies.
     *
     * @param userRepository  the repository used to access user data
     * @param passwordEncoder the encoder used to hash user passwords
     */
    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Loads a user by their username.
     *
     * @param username the username to search for
     * @return the {@link UserDetails} object used by Spring Security
     * @throws UsernameNotFoundException if the user does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("The user does not exist"));

        // Use authorities (not roles) to match SecurityConfig.hasAuthority("ADMIN"/"COMPANY"/"STUDENT")
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(user.getRole()))
                .build();
    }

    /**
     * Registers a new user using the provided registration request.
     *
     * @param request the registration request containing user credentials
     * @return the created user as a {@link UserResponse}
     * @throws ApiException if the username or email already exists
     */
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAddress(request.getAddress());
        user.setCompanyName(request.getCompanyName());
        user.setPhoneNumber(request.getPhoneNumber());

        User savedUser = userRepository.save(user);
        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getAddress(),
                savedUser.getCompanyName(),
                savedUser.getPhoneNumber()
        );
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of {@link UserResponse}.
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getAddress(),
                        user.getCompanyName(),
                        user.getPhoneNumber()
                ))
                .toList();
    }

    /**
     * Updates an existing user by ID with the provided information (no password change here).
     *
     * @param id      the ID of the user to update
     * @param request the updated user data (username, email, role, profile fields)
     * @return the updated user as a {@link UserResponse}
     * @throws ApiException if the user is not found or uniqueness is violated
     */
    public UserResponse updateUser(Long id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found"));

        // Uniqueness checks only if value changed
        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException("Username already taken");
        }
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already in use");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole().toUpperCase());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAddress(request.getAddress());
        user.setCompanyName(request.getCompanyName());
        user.setPhoneNumber(request.getPhoneNumber());

        User savedUser = userRepository.save(user);
        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getAddress(),
                savedUser.getCompanyName(),
                savedUser.getPhoneNumber()
        );
    }

    /**
     * Deletes a user from the database by their ID.
     *
     * @param id the ID of the user to delete
     * @throws ApiException if the user does not exist
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ApiException("User not found");
        }
        userRepository.deleteById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
