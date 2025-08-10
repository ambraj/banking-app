package com.example.userservice.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class LoginResponse {

    private String token;

}
