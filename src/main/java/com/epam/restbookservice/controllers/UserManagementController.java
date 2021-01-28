package com.epam.restbookservice.controllers;

import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.dtos.UserManagementDTO;
import com.epam.restbookservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserManagementController implements SecuredController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<UserManagementDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserManagementDTO::userToUserManagementDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public UserManagementDTO getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(UserManagementDTO::userToUserManagementDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("User with id %s not found.", id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public UserManagementDTO createUser(@RequestBody User user) {
        return userService.createUser(user)
                .map(UserManagementDTO::userToUserManagementDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("User with username %s already exists.", user.getUsername())));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public UserManagementDTO modifyUser(@PathVariable Long userId, @RequestBody User user) {
        return userService.modifyUser(userId, user)
                .map(UserManagementDTO::userToUserManagementDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Something bad happen when modifying user"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
