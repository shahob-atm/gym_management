package com.example.gym_management.dto.user;

import jakarta.validation.constraints.NotBlank;

public record LoginUserDto(
        @NotBlank
        String username,
        @NotBlank
        String password
) {
}
