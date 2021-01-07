package com.epam.restbookservice.controllers;

import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.dtos.LoginDTO;
import com.epam.restbookservice.dtos.SignUpDTO;
import com.epam.restbookservice.services.UserService;
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
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public User signUp(@RequestBody @Valid SignUpDTO signUpDTO) {
        return userService
                .signUp(signUpDTO.getUsername(), signUpDTO.getPassword(), signUpDTO.getFirstName(), signUpDTO.getLastName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists"));
    }

    @PostMapping("/signin")
    public String signIn(@RequestBody @Valid LoginDTO loginDTO) {
        return userService
                .signIn(loginDTO.getUsername(), loginDTO.getPassword())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Login failed"));
    }


}
