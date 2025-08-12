package com.example.jobappbackend.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Service for managing JWT (JSON Web Tokens).
 * Reads secret and expiration from application properties.
 */
@Service
public class JwtService {

    /** Signing key loaded from configuration. */
    private final Key key;

    /** Token expiration in milliseconds. */
    private final long expirationMs;

    /**
     * Constructs the service with secret and expiration from properties.
     *
     * @param secret       JWT secret (at least 32 chars).
     * @param expirationMs token validity in milliseconds.
     */
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:86400000}") long expirationMs
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("jwt.secret must be at least 32 characters long");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT for the given username.
     *
     * @param username subject of the token.
     * @return signed JWT string.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Extracts username from token; returns null if invalid/expired.
     *
     * @param token JWT string.
     * @return username or null.
     */
    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Validates token by subject and expiration.
     *
     * @param token    JWT string.
     * @param username expected subject.
     * @return true if valid, false otherwise.
     */
    public boolean isTokenValid(String token, String username) {
        if (username == null) return false;
        String subject = extractUsername(token);
        return username.equals(subject) && !isTokenExpired(token);
    }

    /**
     * Checks whether the token is expired; returns true if parsing fails.
     *
     * @param token JWT string.
     * @return true if expired/invalid.
     */
    private boolean isTokenExpired(String token) {
        try {
            Date exp = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return exp.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }
}
