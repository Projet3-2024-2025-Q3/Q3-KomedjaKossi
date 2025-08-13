package com.example.jobappbackend.service;

import com.example.jobappbackend.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
     * Generates a signed JWT for the given {@link User}, embedding all main details.
     *
     * @param user the authenticated user entity.
     * @return a signed JWT string.
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("companyName", user.getCompanyName());
        claims.put("address", user.getAddress());
        claims.put("phoneNumber", user.getPhoneNumber());

        return createToken(claims, user.getUsername());
    }

    /**
     * Creates a JWT with the given claims and subject.
     *
     * @param claims  map of claims to embed in the payload.
     * @param subject the token subject (usually the username).
     * @return a signed JWT string.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
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
