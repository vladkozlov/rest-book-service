package com.epam.restbookservice.services;

import com.epam.restbookservice.domain.Role;
import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.repositories.RoleRepository;
import com.epam.restbookservice.repositories.UserRepository;
import com.epam.restbookservice.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService underTest;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtProvider jwtProvider;

    @Test
    void signUp_shouldCreateUser() {
        String username = "username";
        Role role = new Role();
        User savedUser = new User();


        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenReturn(savedUser);
        when(passwordEncoder.encode(any())).thenReturn("S3cr3t");

        Optional<User> optionalUser = underTest.signUp(username, "pw", "John", "Smith");
        assertTrue(optionalUser.isPresent());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signUp_userAlreadyExists_shouldReturnEmpty() {
        String username = "username";
        Role role = new Role();
        User savedUser = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(savedUser));

        Optional<User> optionalUser = underTest.signUp(username, "pw", "John", "Smith");

        assertTrue(optionalUser.isEmpty());
    }

    @Test
    void signIn() {
        User user = new User();
        String username = "username";
        String token = "token";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtProvider.createToken(username, null)).thenReturn(token);

        Optional<String> optionalToken = underTest.signIn(username, "pw");

        assertTrue(optionalToken.isPresent());
    }

    @Test
    void signInWithInvalidCredentials_shouldReturnEmpty() {
        User user = new User();
        String username = "username";
        String token = "token";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        Optional<String> optionalToken = underTest.signIn(username, "pw");

        assertFalse(optionalToken.isPresent());
    }

    @Test
    void getUserByUsername() {
        String username = "username";
        User user = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> optionalUser = underTest.getUserByUsername(username);

        verify(userRepository).findByUsername(username);
        assertThat(optionalUser, is(Optional.of(user)));
    }

    @Test
    void getAllUsers() {
        User user = new User();
        List<User> users = Collections.singletonList(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> allUsers = underTest.getAllUsers();

        assertThat(allUsers, is(users));
    }

    @Test
    void createUser() {
        User user = new User();
        String username = "username";
        user.setUsername(username);
        Role userRole = new Role();
        userRole.setRoleName("USER");
        user.setRoles(Collections.singletonList(userRole));
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName(anyString())).thenReturn(Optional.of(userRole));
        when(userRepository.save(any())).thenReturn(user);

        Optional<User> optionalUser = underTest.createUser(user);

        verify(passwordEncoder).encode(user.getPassword());
        assertTrue(optionalUser.isPresent());
    }

    @Test
    void suspendAccount() {
        String username = "username";
        User user = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        underTest.suspendAccount(username);

        verify(userRepository).save(user);
    }

    @Test
    void getUserById() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        Optional<User> optionalUser = underTest.getUserById(userId);

        assertTrue(optionalUser.isPresent());
    }

    @Test
    void modifyUser() {
        long userId = 3L;
        User user = new User();
        user.setId(userId);

        underTest.modifyUser(userId, user);

        verify(userRepository).save(user);
    }

    @Test
    void deleteUser() {
        long userId = 1L;
        underTest.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void changeUserdata() {

        String oldUsername = "jsmith";
        String firstName = "John";
        String lastName = "Smith";
        String newUsername = "newUsername";
        User userData = new User();
        userData.setFirstName(firstName);
        userData.setLastName(lastName);
        userData.setUsername(newUsername);
        userData.setPassword("password");
        User newUser = new User();

        when(userRepository.existsByUsername(newUsername)).thenReturn(false);
        when(userRepository.findByUsername(oldUsername)).thenReturn(Optional.of(newUser));

        Optional<User> changedUser = underTest.changeUserdata(oldUsername, userData);

        verify(userRepository).save(newUser);
        assertThat(changedUser.get().getUsername(), is(newUsername));
        assertThat(changedUser.get().getFirstName(), is(firstName));
        assertThat(changedUser.get().getLastName(), is(lastName));
    }

    @Test
    void changeUserdata_usernameIsUnavailable_shouldThrow() {
        String oldUsername = "jsmith";
        String newUsername = "newUsername";
        User userData = new User();
        userData.setUsername(newUsername);

        when(userRepository.existsByUsername(newUsername)).thenReturn(true);

        assertThrows(InvalidParameterException.class, () -> {
            underTest.changeUserdata(oldUsername, userData);
        });
    }
}