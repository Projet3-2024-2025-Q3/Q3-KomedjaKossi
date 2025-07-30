package com.example.jobappbackend.service;

import com.example.jobappbackend.dto.RegisterRequest;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    /**
     * Registers a new user using the provided registration request.
     *
     * @param request the registration request containing user credentials
     * @return the created {@link User} entity
     * @throws RuntimeException if the username already exists
     */
    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return userRepository.save(user);
    }
    /**
     * Retrieves all users from the database.
     *
     * @return a list of all {@link User} entities.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Updates an existing user by ID with the provided information.
     *
     * @param id      the ID of the user to update
     * @param request the updated user data (username, email, role, password)
     * @return the updated {@link User} entity
     * @throws RuntimeException if the user is not found in the database
     */
    public User updateUser(Long id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // encode the updated password

        return userRepository.save(user);
    }

    /**
     * Deletes a user from the database by their ID.
     *
     * @param id the ID of the user to delete
     * @throws RuntimeException if the user does not exist
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

}
