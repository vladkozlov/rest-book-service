package com.epam.restbookservice.security;

import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.springframework.security.core.userdetails.User.withUsername;

@Component
public class LibraryUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public LibraryUserDetailsService(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with name " + username + "does not exist."));
        return withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(isDisabled(user))
                .build();
    }

    private boolean isDisabled(User user) {
        return !user.isEnabled();
    }

    public Optional<UserDetails> loadUserByJwtToken(String token) {
        if (jwtProvider.isValidToken(token)) {
            return Optional.of(loadUserByUsername(jwtProvider.getUsername(token)));
        }
        return Optional.empty();
    }
}
