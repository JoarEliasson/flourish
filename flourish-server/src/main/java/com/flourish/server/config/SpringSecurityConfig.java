package com.flourish.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration class.
 * <p>
 * Configures the security filter chain for the application, defining which endpoints are publicly accessible
 * and which require authentication. This configuration integrates with Vaadin and REST endpoints.
 * </p>
 *
 * @version 1.0
 */
@Configuration
public class SpringSecurityConfig {

    /**
     * Configures the security filter chain.
     * <p>
     * The configuration disables CSRF (for demonstration purposes; in production, CSRF protection should be enabled
     * and configured appropriately), and permits access to public endpoints such as login, register, and Vaadin
     * static resources. All other endpoints require authentication.
     * </p>
     *
     * @param http the {@link HttpSecurity} instance to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/login",
                        "/register",
                        "/register/**",
                        "/api/auth/**",
                        "/api/register/**",
                        "/vaadinServlet/**",
                        "/VAADIN/**",
                        "/heartbeat",
                        "/HEARTBEAT",
                        "/frontend/**",
                        "/favicon.ico"
                ).permitAll()
                .requestMatchers("/vaadinServlet/**").permitAll()
                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll()
        );

        return http.build();
    }
}
