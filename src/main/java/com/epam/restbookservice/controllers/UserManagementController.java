package com.epam.restbookservice.controllers;

import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.dtos.UserManagementDTO;
import com.epam.restbookservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "Managing users")
public class UserManagementController implements SecuredController {

    private final UserService userService;

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    @GetMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<UserManagementDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserManagementDTO::userToUserManagementDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get user with borrowed books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public UserManagementDTO getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(UserManagementDTO::userToUserManagementDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("User with id %s not found.", id)));
    }

    @Operation(summary = "Create a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Username already exists",
                    content = @Content)})
    @PostMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public UserManagementDTO createUser(@RequestBody User user) {
        return userService.createUser(user)
                .map(UserManagementDTO::userToUserManagementDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("User with username %s already exists.", user.getUsername())));
    }

    @Operation(summary = "Modify user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modified the user"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public UserManagementDTO modifyUser(@PathVariable Long userId, @RequestBody User user) {
        return userService.modifyUser(userId, user)
                .map(UserManagementDTO::userToUserManagementDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Something bad happen when modifying user"));
    }

    @Operation(summary = "Delete a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted the user"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
