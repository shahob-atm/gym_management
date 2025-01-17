package com.example.gym_management.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDto(
        String adminName,
        @NotBlank
        String username,
        @NotNull
        String password,
        String fullName
) {
}
