package com.example.gym_management.controller;

import com.example.gym_management.dto.user.LoginUserDto;
import com.example.gym_management.dto.user.UserDto;
import com.example.gym_management.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public HttpEntity<?> register(@RequestBody UserDto userDto) {
        return authService.registerUser(userDto);
    }

    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody LoginUserDto loginUserDto) {
        return authService.loginUser(loginUserDto);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(authentication.getAuthorities());
    }
}
