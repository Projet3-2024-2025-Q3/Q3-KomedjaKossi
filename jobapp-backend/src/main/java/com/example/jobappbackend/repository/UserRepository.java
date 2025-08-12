package com.example.jobappbackend.repository;

import com.example.jobappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 * <p>
 * Provides CRUD operations and custom query methods
 * for retrieving users by username or email, and for
 * checking the existence of users based on these fields.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for.
     * @return an Optional containing the found User, or empty if none found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for.
     * @return an Optional containing the found User, or empty if none found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     *
     * @param username the username to check.
     * @return true if a user with the username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email the email address to check.
     * @return true if a user with the email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}
