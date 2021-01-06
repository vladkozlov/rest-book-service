package com.epam.restbookservice.user.controller;

import com.epam.restbookservice.user.domain.User;
import com.epam.restbookservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<User> createBook(@RequestBody User user) {
        return ResponseEntity
                .created(URI.create("/"))
                .body(userService.saveUser(user));
    }
}
