package com.epam.restbookservice.controllers;

import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.dtos.LoginDTO;
import com.epam.restbookservice.dtos.SignUpDTO;
import com.epam.restbookservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Slf4j
@Tag(name = "Authentication", description = "Managing authentication")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created the account"),
            @ApiResponse(responseCode = "400", description = "Invalid username supplied",
                    content = @Content)})
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public User signUp(@RequestBody @Valid SignUpDTO signUpDTO) {
        return userService
                .signUp(signUpDTO.getUsername(), signUpDTO.getPassword(), signUpDTO.getFirstName(), signUpDTO.getLastName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists"));
    }

    @Operation(summary = "Sign in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signed in"),
            @ApiResponse(responseCode = "400", description = "Login failed",
                    content = @Content)})
    @PostMapping("/signin")
    public String signIn(@RequestBody @Valid LoginDTO loginDTO) {
        return userService
                .signIn(loginDTO.getUsername(), loginDTO.getPassword())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Login failed"));
    }

}
