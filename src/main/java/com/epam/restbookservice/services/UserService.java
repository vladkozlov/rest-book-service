package com.epam.restbookservice.services;

import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.exceptions.UserNotExistException;
import com.epam.restbookservice.repositories.RoleRepository;
import com.epam.restbookservice.repositories.UserRepository;
import com.epam.restbookservice.security.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        if (userRepository.findByUsername(username).isEmpty()) {
            var role = roleRepository.findByRoleName("ROLE_USER");

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
        var user = userRepository.findByUsername(username);
        if(user.isPresent()) {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
                token = Optional.of(jwtProvider.createToken(username, user.get().getRoles()));
            } catch (AuthenticationException e) {
                log.warn("Login attempt failed for user: {}", username);
            }
        }
        return token;
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> createUser(User user) {
        Optional<User> newUser = Optional.empty();

        if (userRepository.findByUsername(user.getUsername()).isEmpty()) {
            var roles = user.getRoles()
                    .stream()
                    .map(role -> roleRepository
                            .findByRoleName(role.getRoleName()).get())
                    .collect(Collectors.toList());

            newUser = Optional.of(userRepository.save(new User(
                    user.getUsername(),
                    passwordEncoder.encode(user.getPassword()),
                    user.getFirstName(),
                    user.getLastName(),
                    roles)));
        }

        return newUser;
    }

    public void suspendAccount(String username) {
        var user = userRepository.findByUsername(username);

        user.ifPresent(u -> {
            u.setEnabled(false);
            userRepository.save(u);
        });
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> modifyUser(Long userId, User user) {
        var dbUserOptional = userRepository.findById(userId);

        if (dbUserOptional.isEmpty()) {
            throw new UserNotExistException(String.format("User with id %s not found", userId));
        }

        var u = dbUserOptional.get();

        if (user.getUsername() != null) {
            u.setUsername(user.getUsername());
        }
        if (user.getFirstName() != null) {
            u.setFirstName(user.getFirstName());
        }
        if(user.getLastName() != null) {
            u.setLastName(user.getLastName());
        }
        if (user.getPassword() != null) {
            u.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            u.setRoles(user.getRoles());
        }
        if (user.getBorrowedBooks() != null && !user.getBorrowedBooks().isEmpty()){
            u.setBorrowedBooks(user.getBorrowedBooks());
        }

        return Optional.of(userRepository.save(u));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> changeUserdata(String username, User userData) throws InvalidParameterException {

        var newUsername = userData.getUsername();

        if (newUsername != null && !newUsername.equals(username)) {
            if (userRepository.existsByUsername(newUsername)) {
                throw new InvalidParameterException(String.format("Username %s is unavailable.", newUsername));
            }
        }

        var currentUser = userRepository.findByUsername(username);

        currentUser.ifPresent(user -> {
            user.setFirstName(userData.getFirstName());
            user.setLastName(userData.getLastName());
            user.setUsername(userData.getUsername());

            var password = userData.getPassword();

            if (password != null) {
                user.setPassword(passwordEncoder.encode(password));
            }

            userRepository.save(user);
        });

        return currentUser;
    }

    public Optional<User> getCurrentUser() {
        return userRepository.findByUsername(getCurrentAccountUsername());
    }

    public static String getCurrentAccountUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
