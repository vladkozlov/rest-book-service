package com.epam.restbookservice.controllers;

import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.dtos.BookBorrowDTO;
import com.epam.restbookservice.dtos.UserDTO;
import com.epam.restbookservice.dtos.UserManagementDTO;
import com.epam.restbookservice.services.UserService;
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
public class UserManagementController implements SecuredController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<UserManagementDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(this::userToUserManagementDTO)
                .collect(Collectors.toList());
    }

    private UserManagementDTO userToUserManagementDTO(User user) {
        return UserManagementDTO
                .builder()
                .user(UserDTO.userToUserDTO(user))
                .bookBorrow(user
                        .getBorrowedBooks()
                        .stream()
                        .map(BookBorrowDTO::bookBorrowToBookBorrowDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    @GetMapping
    @RequestMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User with id " + id + " not found."));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "User with username " + user.getUsername() + " already exists."));
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public User modifyUser(@RequestBody User user) {
        return userService.modifyUser(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
