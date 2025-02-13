package com.flourish.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for application security settings.
 * <p>
 * This class defines the {@code PasswordEncoder} bean used to encrypt and
 * verify user passwords using the BCrypt algorithm.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
@Configuration
public class SecurityConfig {

    /**
     * Creates a {@link PasswordEncoder} bean that uses the BCrypt hashing function.
     *
     * @return a {@link PasswordEncoder} instance for password encryption
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
