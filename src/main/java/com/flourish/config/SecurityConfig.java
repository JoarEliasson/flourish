package com.flourish.config;

import com.flourish.security.FlourishAuthenticationManager;
import com.flourish.security.FlourishUserDetailsService;
import com.flourish.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configures Spring Security + Vaadin security using VaadinWebSecurity,
 * closely matching the official Vaadin docs approach.
 *
 * <p>We specify @Route("login") for the login view,
 * then call setLoginView(http, LoginView.class).
 * Unauthenticated users trying to access
 * other routes will be redirected to /login.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-16
 */
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    private final FlourishUserDetailsService myUserDetailsService;

    /**
     * Constructs a new SecurityConfig with our custom DB-based UserDetailsService.
     *
     * @param myUserDetailsService a service that loads users from your MariaDB-based repository
     */
    public SecurityConfig(FlourishUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    /**
     * The standard password encoder for hashing passwords.
     *
     * @return a BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * A standard authentication provider that uses our custom
     * UserDetailsService + BCrypt hashing.
     *
     * @return a DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(myUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Configures HTTP security using VaadinWebSecurity.
     *
     * <p>We do not manually call .formLogin(). Instead, we rely on Vaadin
     * to automatically set up /login as the login-processing URL
     * and /login as the route for login if you specify
     * {@code setLoginView(http, LoginView.class)}.</p>
     *
     * @param http the HttpSecurity instance to configure
     * @throws Exception if an error occurs during security configuration
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/images/**").permitAll();
        });

        super.configure(http);

        setLoginView(http, LoginView.class);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new FlourishAuthenticationManager(myUserDetailsService, bCryptPasswordEncoder());
    }

    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

}
