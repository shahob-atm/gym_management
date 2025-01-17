package com.example.gym_management.service.auth;

import com.example.gym_management.dto.user.LoginUserDto;
import com.example.gym_management.dto.user.UserDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> registerUser(UserDto userDto);

    ResponseEntity<?> loginUser(LoginUserDto loginUserDto);
}
