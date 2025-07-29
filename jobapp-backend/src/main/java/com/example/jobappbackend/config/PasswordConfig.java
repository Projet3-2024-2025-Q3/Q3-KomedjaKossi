package com.example.jobappbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class responsible for providing the password encoder bean.
 * Uses BCrypt algorithm to securely hash user passwords.
 */
@Configuration
public class PasswordConfig {

    /**
     * Creates and returns a BCryptPasswordEncoder instance as a Spring Bean.
     * This encoder will be used for hashing and verifying passwords.
     *
     * @return a PasswordEncoder using BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
