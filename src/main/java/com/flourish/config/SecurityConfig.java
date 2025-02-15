package com.flourish.config;

import com.flourish.security.MyCustomUserDetailsService;
import com.flourish.views.SignInView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationProvider;

import java.util.List;

/**
 * Configures Vaadin + Spring Security, allowing unauthenticated
 * access to the SignInView (which also handles registration).
 *
 * <p>Uses a single route (/signin) for login and registration.
 * After registration, we programmatically authenticate the new user.</p>
 */
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    private final MyCustomUserDetailsService myCustomUserDetailsService;

    public SecurityConfig(MyCustomUserDetailsService myCustomUserDetailsService) {
        this.myCustomUserDetailsService = myCustomUserDetailsService;
    }

    /**
     * Defines a BCryptPasswordEncoder bean.
     *
     * @return The password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes our custom UserDetailsService as a bean.
     *
     * @return The DB-based UserDetailsService.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return myCustomUserDetailsService;
    }

    /**
     * Creates a DAO AuthenticationProvider that uses our custom
     * UserDetailsService and the BCryptPasswordEncoder.
     *
     * @return A configured DaoAuthenticationProvider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(myCustomUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Expose an AuthenticationManager bean so we can programmatically authenticate
     * a user after registration.
     *
     * @param authenticationProvider Our DAO-based provider.
     * @return An AuthenticationManager that uses the given provider.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(List.of(authenticationProvider));
    }

    /**
     * We rely on Vaadin's route-based security for restricting other views.
     * The /signin route is @PermitAll, so anyone can access it to sign in or register.
     *
     * @param http The HttpSecurity to configure
     * @throws Exception in case of errors
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/images/**").permitAll();
        });

        super.configure(http);

        setLoginView(http, SignInView.class);
    }
}
