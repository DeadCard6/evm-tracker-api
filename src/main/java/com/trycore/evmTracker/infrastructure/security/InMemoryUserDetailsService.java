package com.trycore.evmTracker.infrastructure.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class InMemoryUserDetailsService implements UserDetailsService {

    private final String username = "user";
    private final String password = "$2a$10$7f6KpKBhdV/wcQ7JZv1L3Oa4J6T7Z4K9M2Bq0Xe5TqHh2LwN5M5JG"; // example bcrypt hash

    @Override
    public UserDetails loadUserByUsername(String requestedUsername) {
        if (!requestedUsername.equals(username)) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + requestedUsername);
        }

        return User.builder()
                .username(username)
                .password(password)
                .roles("USER")
                .build();
    }
}
