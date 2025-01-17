package com.example.gym_management.service.jwt;

import com.example.gym_management.entity.User;

public interface JwtService {
    String generateJwtToken(User user);

    String extractJwtToken(String token);
}
