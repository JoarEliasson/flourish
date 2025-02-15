package com.flourish.server.config;

import com.flourish.client.views.SignInView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for Vaadin + Spring Security integration.
 * <p>
 * Extends {@link VaadinWebSecurity} to properly handle Vaadin internal routes, static resources,
 * and route-based security. Redirects unauthenticated requests to the "signin" route.
 * </p>
 *
 * @author  Joar Eliasson
 * @version 1.0
 * @since   2025-02-14
 */
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    /**
     * Configures the HTTP security settings, including the custom sign-in view.
     *
     * @param http the {@link HttpSecurity} to configure
     * @throws Exception if an error occurs during configuration
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Let Vaadin manage its own routes and resources
        super.configure(http);

        // Set our custom sign-in route for unauthenticated requests
        setLoginView(http, SignInView.class);

        // Optional: if your app uses iframes or embedded content, adjust frame options:
        // http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // Optional: disable CSRF in dev or if Vaadin handles it differently
        // http.csrf(csrf -> csrf.disable());
    }

    /**
     * Defines a {@link PasswordEncoder} bean using BCrypt for secure password hashing.
     *
     * @return a {@link PasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
