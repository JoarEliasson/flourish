package com.flourish.config;

import com.flourish.views.SignInView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configures Vaadin + Spring Security, specifying the custom sign-in route
 * and role-based access.
 *
 * <p>Utilizes {@link VaadinWebSecurity} to handle the majority
 * of Vaadin-specific security logic.</p>
 *
 * @see <a href="https://vaadin.com/docs/latest/security/spring-security">Vaadin Security Docs</a>
 */
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    /**
     * Configures HTTP security for Vaadin and sets the custom login view.
     *
     * @param http The HttpSecurity object to configure.
     * @throws Exception if an error occurs during configuration.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // (Optionally) allow public access to certain routes or resources here
        // e.g. http.authorizeHttpRequests().requestMatchers("/images/*.png").permitAll();

        super.configure(http);

        // Use the VaadinWebSecurity method to set the sign-in view
        setLoginView(http, SignInView.class);
    }

    /**
     * Defines a password encoder bean.
     *
     * @return a BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
