package com.epam.restbookservice.controllers;

import com.epam.restbookservice.domain.Role;
import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.dtos.UserDTO;
import com.epam.restbookservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserDTO getCurrentAccountData() {
        var currentUsername = getCurrentAccountUsername();

        return userService.getUserByUsername(currentUsername)
                .map(this::userToUserDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    }

    @PostMapping("/suspend")
    public ResponseEntity<Void> suspendAccount() {
        var currentUsername = getCurrentAccountUsername();

        userService.suspendAccount(currentUsername);
        return ResponseEntity.ok().build();
    }

    private String getCurrentAccountUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private UserDTO userToUserDTO(User user) {
        return UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .isEnabled(user.isEnabled())
                .roles(getListOfStringRoles(user))
                .build();
    }

    private List<String> getListOfStringRoles(User user) {
        return user.getRoles()
                .stream()
                .map(Role::getRoleName)
                .map(getOriginalRoleName())
                .collect(Collectors.toList());
    }

    private Function<String, String> getOriginalRoleName() {
        return role -> role.substring(5);
    }
}
