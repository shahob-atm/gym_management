package com.example.gym_management.dto.gym;

import jakarta.validation.constraints.NotBlank;

public record GymAdminDto(
        @NotBlank
        String GymId,
        @NotBlank
        String fullName,
        @NotBlank
        String password,
        @NotBlank
        String username
) {
}
