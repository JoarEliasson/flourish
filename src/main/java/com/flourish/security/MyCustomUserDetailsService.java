package com.flourish.security;

import com.flourish.domain.User;
import com.flourish.domain.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Loads user data from a MariaDB-based UserRepository.
 * Replaces the default in-memory approach from the Vaadin docs.
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-15
 */
@Service
public class MyCustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs the service with our JPA repository.
     */
    public MyCustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their email (used as the username).
     *
     * @param username email address
     * @return a Spring Security UserDetails if found
     * @throws UsernameNotFoundException if no matching user is found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User dbUser = userRepository.findByEmail(username);
        if (dbUser == null) {
            throw new UsernameNotFoundException("No user found for email " + username);
        }
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(dbUser.getRole());
        return new org.springframework.security.core.userdetails.User(
                dbUser.getEmail(),
                dbUser.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
