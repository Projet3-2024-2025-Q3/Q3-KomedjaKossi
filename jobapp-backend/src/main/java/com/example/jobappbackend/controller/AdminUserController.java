package com.example.jobappbackend.controller;

import com.example.jobappbackend.dto.RegisterRequest;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller that handles user management operations for administrators.
 * Only accessible to users with the role {@code ADMIN}.
 */
@RestController
@RequestMapping("/admin/users")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * Retrieves all registered users.
     *
     * @return a list of all {@link User} entities
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Creates a new user using the provided registration data.
     *
     * @param request the {@link RegisterRequest} containing user information
     * @return the newly created {@link User}
     */
    @PostMapping
    public User createUser(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    /**
     * Updates an existing user identified by ID.
     *
     * @param id      the ID of the user to update
     * @param request the {@link RegisterRequest} with updated user info
     * @return the updated {@link User}
     */
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody RegisterRequest request) {
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
