package com.example.userservice.controller;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.RedisTestService;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RedisTestService redisTestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public UserResponse registerUser(@Valid @RequestBody UserRequest userRequest) {
        redisTestService.testConnection();
        log.info("Received request to register user for username: {}", userRequest.getUsername());
        return userService.saveUser(userRequest);
    }

}
