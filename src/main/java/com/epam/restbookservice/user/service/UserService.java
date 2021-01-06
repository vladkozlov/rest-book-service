package com.epam.restbookservice.user.service;

import com.epam.restbookservice.user.domain.User;
import com.epam.restbookservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
