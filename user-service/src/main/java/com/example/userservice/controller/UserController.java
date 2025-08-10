package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserRegistrationResponse;
import com.example.userservice.service.RedisTestService;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RedisTestService redisTestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public UserRegistrationResponse registerUser(@Valid @RequestBody UserDto userDto) {
        redisTestService.testConnection();
        log.info("Received request to register user for username: {}", userDto.getUsername());
        return userService.saveUser(userDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/all")
    public List<UserDto> fetchAllUsers() {
        log.info("Received request to fetch all users");
        return userService.fetchAllUsers();
    }
}
