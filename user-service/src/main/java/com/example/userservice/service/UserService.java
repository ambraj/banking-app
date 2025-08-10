package com.example.userservice.service;

import com.example.userservice.constants.Constants;
import com.example.userservice.constants.Role;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserRegistrationResponse;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.exception.ConflictException;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

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

}
