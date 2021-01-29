package com.epam.restbookservice.controllers;

import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.dtos.BookBorrowDTO;
import com.epam.restbookservice.dtos.UserDTO;
import com.epam.restbookservice.services.BookBorrowService;
import com.epam.restbookservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/account")
@Tag(name = "Accounts", description = "Managing accounts")
public class AccountController implements SecuredController {

    private final UserService userService;
    private final BookBorrowService bookBorrowService;

    public AccountController(UserService userService, BookBorrowService bookBorrowService) {
        this.userService = userService;
        this.bookBorrowService = bookBorrowService;
    }

    @Operation(summary = "Get account details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details found"),
            @ApiResponse(responseCode = "400", description = "Invalid user supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    @GetMapping
    public UserDTO getCurrentAccountData() {
        var currentUsername = getCurrentAccountUsername();

        return userService.getUserByUsername(currentUsername)
                .map(UserDTO::userToUserDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    }

    @Operation(summary = "Get borrowed books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrowed books found"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content) })
    @GetMapping("/borrowedBooks")
    public List<BookBorrowDTO> getCurrentAccountBorrowedBooks() {
        return bookBorrowService.getBorrowedBooksForCurrentAccount()
                .stream()
                .map(BookBorrowDTO::bookBorrowToBookBorrowDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Suspend account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account suspended"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
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

    @Operation(summary = "Change account details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details changed"),
            @ApiResponse(responseCode = "400", description = "Invalid data supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    @PostMapping
    public UserDTO changeUserData(@RequestBody UserDTO user) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var currentUsername = authentication.getName();

        return userService.changeUserdata(currentUsername, userOfUserDTO(user))
                .map(UserDTO::userToUserDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    }

    private User userOfUserDTO(UserDTO userDTO) {
        return new User(userDTO.getUsername(), userDTO.getPassword(), userDTO.getFirstName(), userDTO.getLastName());
    }

}
