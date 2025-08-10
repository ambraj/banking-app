package com.example.userservice.service;

import com.example.userservice.constants.Constants;
import com.example.userservice.constants.Role;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.LoginResponse;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserRegistrationResponse;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.exception.ConflictException;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserRegistrationResponse saveUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new ConflictException(String.format("Username %s already exists!", userDto.getUsername()));
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException(String.format("Email %s already exists!", userDto.getEmail()));
        }
        UserEntity userEntity = userMapper.toUserEntity(userDto);
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userEntity.setRole("ROLE_" + (userDto.getRole() != null ? userDto.getRole() : Role.USER.getRoleName()));
        userEntity = userRepository.save(userEntity);

        return UserRegistrationResponse.builder()
                .userId(userEntity.getId())
                .message(Constants.USER_REGISTERED)
                .build();
    }

    public List<UserDto> fetchAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserDto).toList();
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
                UserEntity userEntity = userRepository.findByUsername(loginRequest.getUsername())
                        .orElseThrow(() -> new ConflictException("Username is not registered!"));
                String token = jwtTokenProvider.generateToken(userEntity.getUsername(), userEntity.getRole());
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setToken(token);
                return loginResponse;
            } else {
                throw new ConflictException("Invalid username or password!");
            }
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid credentials", e);
        }
    }
}
