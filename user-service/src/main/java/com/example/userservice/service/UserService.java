package com.example.userservice.service;

import com.example.userservice.constants.Constants;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.exception.ConflictException;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponse saveUser(UserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new ConflictException(String.format("Username %s already exists!", userRequest.getUsername()));
        }
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new ConflictException(String.format("Email %s already exists!", userRequest.getEmail()));
        }
        UserEntity userEntity = userMapper.toUserEntity(userRequest);
        userEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userEntity = userRepository.save(userEntity);

        return UserResponse.builder()
                .userId(userEntity.getId())
                .message(Constants.USER_REGISTERED)
                .build();
    }

}
