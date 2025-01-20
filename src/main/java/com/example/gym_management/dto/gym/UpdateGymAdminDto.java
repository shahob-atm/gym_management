package com.example.gym_management.dto.gym;

import jakarta.validation.constraints.NotBlank;

public record UpdateGymAdminDto(
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
