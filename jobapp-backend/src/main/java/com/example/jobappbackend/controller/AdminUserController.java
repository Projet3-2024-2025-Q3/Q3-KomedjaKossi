package com.example.jobappbackend.controller;

import com.example.jobappbackend.dto.RegisterRequest;
import com.example.jobappbackend.dto.UserResponse;
import com.example.jobappbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller handling user management operations for administrators.
 * Accessible via routes under /admin/users (secured by SecurityConfig).
 */
@RestController
@RequestMapping("/admin/users")
@CrossOrigin(origins = "*")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * Retrieves all registered users.
     *
     * @return a list of {@link UserResponse}
     */
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Creates a new user using the provided registration data.
     *
     * @param request the {@link RegisterRequest} containing user information
     * @return the created {@link UserResponse}
     */
    @PostMapping
    public UserResponse createUser(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    /**
     * Updates an existing user identified by ID.
     *
     * @param id      the ID of the user to update
     * @param request the {@link RegisterRequest} with updated user info
     * @return the updated {@link UserResponse}
     */
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody RegisterRequest request) {
        return userService.updateUser(id, request);
    }

    /**
     * Deletes a user based on their ID.
     *
     * @param id the ID of the user to delete
     * @return HTTP 200 OK if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
