package com.example.gym_management.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDto(
        @NotBlank
        String fullName,
        @NotNull
        String password,
        @NotBlank
        String username
) {
}
