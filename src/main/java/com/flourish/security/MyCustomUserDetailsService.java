package com.flourish.security;

import com.flourish.domain.User;
import com.flourish.domain.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * A custom UserDetailsService that retrieves user information from the
 * database using the {@link UserRepository}.
 *
 * <p>By defining this service as a bean, we override the default
 * Spring Boot security user. This ensures that authentication
 * is handled against our own user entities.</p>
 *
 * @see org.springframework.security.core.userdetails.UserDetailsService
 */
@Service
public class MyCustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs a new MyCustomUserDetailsService.
     *
     * @param userRepository The repository to load user data.
     */
    public MyCustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their email (used as username).
     *
     * @param email The email used as username credential.
     * @return A Spring Security UserDetails object.
     * @throws UsernameNotFoundException If no user is found with the given email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("No user found for email: " + email);
        }

        // Build a list of authorities. For a single role, a simple collection suffices
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

        // Return a Spring Security UserDetails instance
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),  // already bcrypt-encoded
                Collections.singletonList(authority)
        );
    }
}
