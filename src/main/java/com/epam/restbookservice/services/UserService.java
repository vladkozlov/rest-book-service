package com.epam.restbookservice.services;

import com.epam.restbookservice.domain.Role;
import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.repositories.RoleRepository;
import com.epam.restbookservice.repositories.UserRepository;
import com.epam.restbookservice.security.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;


    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    public Optional<User> signUp(String username, String password, String firstName, String lastName) {
        Optional<User> user = Optional.empty();

        if (!userRepository.findByUsername(username).isPresent()) {
            Optional<Role> role = roleRepository.findByRoleName("ROLE_USER");

            if (role.isPresent()) {
                user = Optional.of(userRepository.save(new User(
                        username,
                        passwordEncoder.encode(password),
                        firstName,
                        lastName,
                        role.get())));
            }
        }

        return user;
    }

    public Optional<String> signIn(String username, String password) {
        Optional<String> token = Optional.empty();
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()) {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
                token = Optional.of(jwtProvider.createToken(username, user.get().getRoles()));
            } catch (AuthenticationException e) {
                log.info("Log in failed for {}", username);
            }
        }
        return token;
    }
}
