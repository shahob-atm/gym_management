package com.example.gym_management.dto.superAdmin;

import jakarta.validation.constraints.NotBlank;

public record SuperAdminDto(
        @NotBlank
        String fullName,
        @NotBlank
        String password,
        @NotBlank
        String username
) {
}
